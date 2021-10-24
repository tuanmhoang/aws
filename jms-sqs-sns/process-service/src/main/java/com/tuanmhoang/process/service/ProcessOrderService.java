package com.tuanmhoang.process.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.item.ItemData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessOrderService {
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	private final DataService dataService;
	private Gson gson = new Gson();
	
	private Map<Long, ItemData> appData;
	
	@Value("${sqs.queue.name:process-q}")
	private String queueName;

	@Autowired
	public ProcessOrderService(DataService dataService) {
		this.dataService = dataService;
		log.info("... Getting app data ...");
		this.appData = dataService.getAppData();
	}

	@Value("${sqs.queue.name:order-standard-q}")
	private String orderQueueName;

	@Value("${sqs.queue.name:order-result-q}")
	private String processQueueName;

	public void checkForQueueAndProcess() {
		// receive messages from the queue
		log.info("receiving messages from queue");
		List<String> messages = getMessagesFromOrderQueue();
		if (messages.size() > 0) {
//			log.info("Getting app data");
//			appData = dataService.getAppData();
			processMessagesFromOrderQueue(messages);
		} else {
			log.info("There is no new message from queue");
		}
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
		// should handle some exceptions about not existing id, etc. but to keep it
		// simple, lets skip these.
		gson.fromJson(mes, null);
		// TODO: delete message after handle logic
	}

	public Map<Long, ItemData> getAppData() {
		return appData;
	}

}
