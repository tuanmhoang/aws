package com.tuanmhoang.order.service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.OrderedItem;
import com.tuanmhoang.order.dtos.OrderedTransaction;
import com.tuanmhoang.order.service.OrderService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderUsingSqs implements OrderService {

	private Gson gson = new Gson();
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	
	@Value("${sqs.queue.name:order-q}")
	private String queueName;
	
	@Override
	public void process(List<OrderedItem> orderedItems) {
		log.info("sending to <process> SQS...");
		OrderedTransaction tx = createTransaction(orderedItems);
		try {
			String dataToSqs = gson.toJson(tx);
			sendSqs(dataToSqs, queueName);
		} catch (Exception e) {
			log.error("Cannot process data to queue... ", e);
		}
		log.info("-- done --");
	}

	// simulate a transaction
	private OrderedTransaction createTransaction(List<OrderedItem> orderedItems) {
		return new OrderedTransaction(UUID.randomUUID().toString(), orderedItems);
	}

	private void sendSqs(String dataToSqs, String queueName) {
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl(); 
        
        SendMessageRequest msgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(dataToSqs)
                .withDelaySeconds(5);
        sqs.sendMessage(msgRequest);
	}
}
