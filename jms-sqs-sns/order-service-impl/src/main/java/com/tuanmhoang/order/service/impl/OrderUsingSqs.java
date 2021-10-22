package com.tuanmhoang.order.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.tuanmhoang.dtos.OrderedItem;
import com.tuanmhoang.dtos.OrderedTransaction;
import com.tuanmhoang.order.service.OrderService;

@Service
public class OrderUsingSqs implements OrderService {

	private static final String QUEUE_NAME = "order-standard-q";
	private Gson gson = new Gson();
	
	@Override
	public void process(List<OrderedItem> orderedItems) {
		System.out.println("sending to sqs...");
		OrderedTransaction tx = createTransaction(orderedItems);
		try {
			String dataToSqs = gson.toJson(tx);
			sendSqs(dataToSqs);
		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("done");
	}

	// simulate a transaction
	private OrderedTransaction createTransaction(List<OrderedItem> orderedItems) {
		return new OrderedTransaction(UUID.randomUUID().toString(), orderedItems);
	}

	private void sendSqs(String dataToSqs) {
//		final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//		        .withRegion(Region.getRegion(Regions.US_EAST_2).getName())
//		        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
		
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl(); 
        
        SendMessageRequest msgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(dataToSqs)
                .withDelaySeconds(5);
        sqs.sendMessage(msgRequest);


//        // Send multiple messages to the queue
//        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
//                .withQueueUrl(queueUrl)
//                .withEntries(
//                        new SendMessageBatchRequestEntry(
//                                "msg_1", "Hello from message 1"),
//                        new SendMessageBatchRequestEntry(
//                                "msg_2", "Hello from message 2")
//                                .withDelaySeconds(10));
//        sqs.sendMessageBatch(send_batch_request);
//
//        // receive messages from the queue
//        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
//        
//        for (Message message : messages) {
//			System.out.println(message.getBody().toString());
//		}
//
//        // delete messages from the queue
//        for (Message m : messages) {
//            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
//        }
		
	}

		


}
