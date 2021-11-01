package com.tuanmhoang.process.service;

import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.OrderedTransaction;
import com.tuanmhoang.order.dtos.item.ItemData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessOrderService {
    private final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();

    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    private final DataService dataService;

    private Gson gson = new Gson();

    private Map<Integer, ItemData> appData;

    @Value("${sns.topic.process.name:processed-order}")
    private String topicName;

    @Value("${sqs.queue.order.name:order-q}")
    private String orderQueueName;

    @Autowired
    public ProcessOrderService(DataService dataService) {
        this.dataService = dataService;
        log.info("... Getting app data ...");
        loadAppData();
    }

    public void checkForQueueAndProcess() {
        // receive messages from the queue
        log.info("receiving messages from queue");
        String queueUrl = sqsClient.getQueueUrl(orderQueueName).getQueueUrl();
        var messages = sqsClient.receiveMessage(queueUrl).getMessages();
        List<String> messagesAsString = messages.stream()
            .map(Message::getBody)
            .collect(Collectors.toList()
            );

        if (messages.isEmpty()) {
            log.info("There is no new message from queue");
        } else {
            processMessagesFromOrderQueue(messagesAsString);
            log.info("finish processing all the messages");
            // delete messages after process
            for (Message m : messages) {
                sqsClient.deleteMessage(queueUrl, m.getReceiptHandle());
            }
            log.info("finish processing all the processed messages");
        }
    }

    private String getSnsTopicArn() throws AmazonSNSException {
        CreateTopicRequest request = new CreateTopicRequest(topicName);
        CreateTopicResult result = snsClient.createTopic(request);
        return result.getTopicArn();
    }

    private void loadAppData() {
        this.appData = dataService.getAppData();
    }

    private void processMessagesFromOrderQueue(List<String> messages) {
        messages.forEach(this::processSingleMessage);
    }

    private void processSingleMessage(String msg) {
        // should handle some exceptions about not existing id, etc. but to keep it
        // simple, lets skip these.
        OrderedTransaction orderedTransaction = gson.fromJson(msg, OrderedTransaction.class);
        orderedTransaction.getOrderedItems()
            .forEach(item -> processSingleItem(orderedTransaction.getId(),
                    item.getId(),
                    item.getQuantity()
                )
            );
    }

    private void processSingleItem(String txId, int itemId, int quantity) {
        int maxAllowed = appData.get(itemId).getMaxAllowed();
        if (quantity > maxAllowed) {
            sendToSns("Rejected",
                String.format("TxId: %s - itemID: %d - Ordered: %d - The ordered items is more than the max allowed number %s.", txId,
                    itemId,
                    quantity,
                    maxAllowed
                )
            );
        } else {
            sendToSns("Accepted", String.format(
                    "TxId: %s - itemID: %d - Ordered: %d - The items is ordered successfully, not more than max allowed number %s ", txId,
                    itemId,
                    quantity,
                    maxAllowed
                )
            );
        }
    }

    private void sendToSns(String subject, String message) {
        PublishRequest request = new PublishRequest(getSnsTopicArn(), message, subject);

        Map<String, MessageAttributeValue> mgsAttrMap = new HashMap<>();
        MessageAttributeValue mgsAttr = new MessageAttributeValue();
        mgsAttr.setDataType("String");
        mgsAttr.setStringValue(subject.toLowerCase());
        mgsAttrMap.put("order_status", mgsAttr);
        request.setMessageAttributes(mgsAttrMap);

        PublishResult result = snsClient.publish(request);
        log.info("MessageId {} - Message sent. Status is {}",
            result.getMessageId(),
            result.getSdkHttpMetadata().getHttpStatusCode()
        );
    }
}