package com.tuanmhoang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.tuanmhoang.dtos.OrderedItems;

@Service
public class OrderUsingSqs implements OrderService {

	private static final String QUEUE_NAME = "order-standard-q";
	
	@Override
	public void process(OrderedItems orderedItems) {
		System.out.println("sending to sqs");
		try {
			sendSqs();
		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("done");
	}

	private void sendSqs() {
//		final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//		        .withRegion(Region.getRegion(Regions.US_EAST_2).getName())
//		        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
		
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl(); 
        
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("hello world")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);


        // Send multiple messages to the queue
        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(
                        new SendMessageBatchRequestEntry(
                                "msg_1", "Hello from message 1"),
                        new SendMessageBatchRequestEntry(
                                "msg_2", "Hello from message 2")
                                .withDelaySeconds(10));
        sqs.sendMessageBatch(send_batch_request);

        // receive messages from the queue
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        
        for (Message message : messages) {
			System.out.println(message.getBody().toString());
		}

        // delete messages from the queue
        for (Message m : messages) {
            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
        }
		
	}

		


}
