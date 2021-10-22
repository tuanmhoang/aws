package com.tuanmhoang.process.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.tuanmhoang.process.config.ScheduledTasks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessOrderService {
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	private Gson gson = new Gson();

	@Value("${sqs.queue.name:order-standard-q}")
	private String orderQueueName;

	@Value("${sqs.queue.name:order-result-q}")
	private String processQueueName;

	public void checkForQueueAndProcess() {
		// receive messages from the queue
		log.info("receiving messages from queue");
		List<String> messages = getMessagesFromOrderQueue();
		processMessagesFromOrderQueue(messages);
	}

	private List<String> getMessagesFromOrderQueue() {
		String queueUrl = sqs.getQueueUrl(orderQueueName).getQueueUrl();
		List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
		return messages.stream().map(mes -> mes.getBody().toString()).collect(Collectors.toList());
	}

	private void processMessagesFromOrderQueue(List<String> messages) {
		messages.forEach(mes -> processSingleMessage(mes));

	}

	private void processSingleMessage(String mes) {
		// TODO: process and send to process queue
	}

}
