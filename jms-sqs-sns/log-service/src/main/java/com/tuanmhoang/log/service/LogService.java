package com.tuanmhoang.log.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogService {

	private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	private static final String TXT_FILE_EXTENSION = ".txt";
	private static final String HYPHEN = "_";

	@Value("${sqs.queue.name:accepted-order-q}")
	private String acceptedQueueName;

	@Value("${sqs.queue.name:rejected-order-q}")
	private String rejectedQueueName;

	@Value("${sqs.queue.name:items-order-bucket}")
	private String bucketName;

	public void checkForQueueAndProcess() {
		// TODO check for messages on queue
		log.info("receiving messages from queue");
		List<String> messagesFromAcceptedQueue = getMessagesFromQueue(acceptedQueueName);
		List<String> messagesFromRejectedQueue = getMessagesFromQueue(rejectedQueueName);
		// process message, write log
		if (messagesFromAcceptedQueue.size() == 0 && messagesFromRejectedQueue.size() == 0) {
			log.info("There is no new message from queue");
		} else {
			processMessagesFromQueue(messagesFromAcceptedQueue);
			processMessagesFromQueue(messagesFromRejectedQueue);
		}
		// delete the processed message

	}

	private List<String> getMessagesFromQueue(String queueName) {
		String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
		List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
		return messages.stream().map(mes -> mes.getBody().toString()).collect(Collectors.toList());
	}

	private void processMessagesFromQueue(List<String> messages) {
		messages.forEach(mes -> processSingleMessage(mes));
	}

	private void processSingleMessage(String mes) {
		List<Bucket> listBuckets = s3.listBuckets();
		listBuckets.forEach(b -> System.out.println(b.getName()));
//		GetObjectRequest getObject = GetObjectRequest.
		DateTime now = DateTime.now(DateTimeZone.UTC);
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(now.getYear()).append(HYPHEN)
						.append(now.getMonthOfYear()).append(HYPHEN)
						.append(now.getDayOfMonth()).append(TXT_FILE_EXTENSION);

		try {
		S3Object s3Object = s3.getObject(bucketName, fileNameBuilder.toString());
		// update file
		} catch(AmazonS3Exception e) {
			log.info(e.getMessage());
			log.info("Creating file...");
			if(e.getStatusCode()== HttpStatus.SC_NOT_FOUND) {
				// create file
				// upload
			}
		}
		
// https://stackoverflow.com/questions/28568635/read-aws-s3-file-to-java-code
	}

}
