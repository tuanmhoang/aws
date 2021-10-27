package com.tuanmhoang.process.service;

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

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
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
    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    private final AmazonSNS snsClient = AmazonSNSClient.builder()
        .withRegion(Region.getRegion(Regions.US_EAST_2).getName())
        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
        .build();

    private final DataService dataService;

    private Gson gson = new Gson();

    private Map<Integer, ItemData> appData;

    @Value("${sns.topic.process.name:processed-order}")
    private String topicName;

    @Autowired
    public ProcessOrderService(DataService dataService) {
        this.dataService = dataService;
        log.info("... Getting app data ...");
        loadAppData();
    }

    @Value("${sqs.queue.name:order-q}")
    private String orderQueueName;

    public void checkForQueueAndProcess() {
        // receive messages from the queue
        log.info("receiving messages from queue");
        String queueUrl = sqs.getQueueUrl(orderQueueName).getQueueUrl();
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        List<String> messagesAsString = messages.stream().map(mes -> mes.getBody()).collect(Collectors.toList());

        if (messages.size() > 0) {
            processMessagesFromOrderQueue(messagesAsString);
            log.info("finish processing all the messages");
            // delete messages after process
            for (Message m : messages) {
                sqs.deleteMessage(queueUrl, m.getReceiptHandle());
            }
        } else {
            log.info("There is no new message from queue");
        }
    }

    private String getSnsTopicArnByTopicName(String topicName) throws AmazonSNSException {
        CreateTopicRequest request = new CreateTopicRequest(topicName);
        CreateTopicResult result = snsClient.createTopic(request);
        return result.getTopicArn();
    }

    private void loadAppData() {
        this.appData = dataService.getAppData();
    }

    private void processMessagesFromOrderQueue(List<String> messages) {
        messages.forEach(mes -> processSingleMessage(mes));
    }

    private void processSingleMessage(String mes) {
        // should handle some exceptions about not existing id, etc. but to keep it
        // simple, lets skip these.
        OrderedTransaction orderedTransaction = gson.fromJson(mes, OrderedTransaction.class);
        orderedTransaction.getOrderedItems()
            .forEach(item -> processSingleItem(orderedTransaction.getId(), item.getId(), item.getQuantity()));
    }

    private void processSingleItem(String txId, int itemId, int quantity) {
        int maxAllowed = appData.get(itemId).getMaxAllowed();
        String message = null;
        String subject = null;
        if (quantity > maxAllowed) {
            subject = "Rejected";
            message =
                String.format("TxId: %s - itemID: %d - Ordered:%d - The ordered items is more than the allowed number %s.", txId, itemId,
                    quantity, maxAllowed);
        } else {
            subject = "Accepted";
            message = String.format("TxId: %s - itemID: %d - Ordered:%d - The items is ordered successfully.", txId, itemId, quantity);
        }
        sendToSns(subject, message);
    }

    private void sendToSns(String subject, String message) {
        PublishRequest request = new PublishRequest(getSnsTopicArnByTopicName(topicName), message, subject);
        Map<String, MessageAttributeValue> mgsAttrMap = new HashMap<>();

        MessageAttributeValue mgsAttr = new MessageAttributeValue();
        mgsAttr.setDataType("String");
        mgsAttr.setStringValue(subject.toLowerCase());
        mgsAttrMap.put("order_status", mgsAttr);
        request.setMessageAttributes(mgsAttrMap);

        PublishResult result = snsClient.publish(request);
        log.info(result.getMessageId() + " Message sent. Status is " + result.getSdkHttpMetadata().getHttpStatusCode());
        log.info("done sending");

    }

    public Map<Integer, ItemData> getAppData() {
        return appData;
    }

}
