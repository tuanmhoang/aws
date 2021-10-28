package notification.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    @Value("${sqs.queue.name.order.accepted:order-accepted-notification-q}")
    private String orderAcceptedQueueName;

    @Value("${sqs.queue.name.order.rejected:order-rejected-notification-q}")
    private String orderRejectedQueueName;

    @Value("${sns.topic.notification.name:notification-change}")
    private String topicName;

    public void checkForQueueAndProcess() {

        String topicArn = getSnsTopicArnByTopicName(topicName);

        // receive messages from the orderAcceptedQueue
        processMessages(orderAcceptedQueueName);

        // receive messages from the orderRejectedQueue
        processMessages(orderRejectedQueueName);
    }

    private String getSnsTopicArnByTopicName(String topicName) throws AmazonSNSException {
        CreateTopicRequest request = new CreateTopicRequest(topicName);
        CreateTopicResult result = snsClient.createTopic(request);
        return result.getTopicArn();
    }

    private void processMessages(String queueName) {
        log.info("Receiving messages from {}", queueName);
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        if (messages.size() > 0) {
            List<String> messagesAsString = messages.stream().map(mes -> mes.getBody()).collect(Collectors.toList());
            processMessagesFromOrderQueue(messagesAsString);
            log.info("finish processing all the messages from {}", queueName);
            // delete messages after process
            for (Message m : messages) {
                sqs.deleteMessage(queueUrl, m.getReceiptHandle());
            }
        } else {
            log.info("There is no new message from {}", queueName);
        }
    }

    private void processMessagesFromOrderQueue(List<String> messages) {
        messages.forEach(mes -> processSingleMessage(mes));
    }

    private void processSingleMessage(String message) {
        PublishRequest request = new PublishRequest(getSnsTopicArnByTopicName(topicName), message, "Order process completed");
        PublishResult result = snsClient.publish(request);
        log.info(result.getMessageId() + " Message sent. Status is " + result.getSdkHttpMetadata().getHttpStatusCode());
        log.info("done sending");
    }

}
