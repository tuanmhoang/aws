package com.tuanmhoang.log.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.tuanmhoang.log.fileextension.FileExtension;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class LogService {

    private final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();

    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    private static final String HYPHEN = "_";

    @Value("${sqs.queue.name:accepted-order-q}")
    private String acceptedQueueName;

    @Value("${sqs.queue.name:rejected-order-q}")
    private String rejectedQueueName;

    @Value("${sqs.queue.name:items-order-bucket}")
    private String bucketName;

    @Value("${sqs.queue.name:accepted}")
    private String acceptedDir;

    @Value("${sqs.queue.name:rejected}")
    private String rejectedDir;

    public void checkForQueueAndProcess() {
        // TODO check for messages on queue
        log.info("receiving messages from queue");
//        List<String> messagesFromAcceptedQueue = getMessagesFromQueue(acceptedQueueName);
//        List<String> messagesFromRejectedQueue = getMessagesFromQueue(rejectedQueueName);
        String acceptedQueueUrl = sqsClient.getQueueUrl(acceptedQueueName).getQueueUrl();
        List<Message> acceptedMessages = sqsClient.receiveMessage(acceptedQueueUrl).getMessages();
        List<String> messagesFromAcceptedQueue = acceptedMessages.stream().map(mes -> mes.getBody()).collect(Collectors.toList());

        String rejectedQueueUrl = sqsClient.getQueueUrl(rejectedQueueName).getQueueUrl();
        List<Message> rejectedMessages = sqsClient.receiveMessage(rejectedQueueUrl).getMessages();
        List<String> messagesFromRejectedQueue = rejectedMessages.stream().map(mes -> mes.getBody()).collect(Collectors.toList());

        // process message, write log
        if (CollectionUtils.isEmpty(messagesFromAcceptedQueue) && CollectionUtils.isEmpty(messagesFromRejectedQueue)) {
            log.info("There is no new message from queue");
        } else {
            if (!CollectionUtils.isEmpty(messagesFromAcceptedQueue)) {
                log.info("Processing messagesFromAcceptedQueue...");
                processMessagesFromAcceptedQueue(messagesFromAcceptedQueue);
                log.info("Done processing messagesFromAcceptedQueue - Deleting messages...");
                for (Message m : acceptedMessages) {
                    DeleteMessageResult deleteMessageResult = sqsClient.deleteMessage(acceptedQueueUrl, m.getReceiptHandle());
                    int statusCode = deleteMessageResult.getSdkHttpMetadata().getHttpStatusCode();
                    log.info("Deleted message << {} >> with statusCode: {} ", m.getBody(), statusCode);
                }
            }
            if (!CollectionUtils.isEmpty(messagesFromRejectedQueue)) {
                log.info("Processing messagesFromRejectedQueue...");
                processMessagesFromRejectedQueue(messagesFromRejectedQueue);
                log.info("Done processing messagesFromRejectedQueue - Deleting messages...");
                for (Message m : rejectedMessages) {
                    DeleteMessageResult deleteMessageResult = sqsClient.deleteMessage(rejectedQueueUrl, m.getReceiptHandle());
                    int statusCode = deleteMessageResult.getSdkHttpMetadata().getHttpStatusCode();
                    log.info("Deleted message << {} >> with statusCode: {} ", m.getBody(), statusCode);
                }
            }

        }
        // delete the processed message

    }

    private List<String> getMessagesFromQueue(String queueName) {
        String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
        List<Message> messages = sqsClient.receiveMessage(queueUrl).getMessages();
        return messages.stream().map(mes -> mes.getBody().toString()).collect(Collectors.toList());
    }

    private void processMessagesFromAcceptedQueue(List<String> messagesFromAcceptedQueue) {
        processMessagesFromQueue(bucketName + "/" + acceptedDir, messagesFromAcceptedQueue, "accepted");
    }

    private void processMessagesFromRejectedQueue(List<String> messagesFromRejectedQueue) {
        processMessagesFromQueue(bucketName + "/" + rejectedDir, messagesFromRejectedQueue, "rejected");
    }

    private void processMessagesFromQueue(String bucketDirLocation, List<String> messagesFromQueue, String type) {
        // build file name
        String reportFileName = buildReportFileName(type);
        // build message to write
        StringBuilder msgToWriteBuilder = new StringBuilder();
        for (String msg : messagesFromQueue) {// TODO
            //log.info("Processing message: {}", msg);
            JSONObject jsonObject = new JSONObject(msg);
            msgToWriteBuilder.append(buildMessageToWrite(jsonObject));
        }
        String msgToWrite = msgToWriteBuilder.toString();
        log.info("msgToWrite : {}", msgToWrite);

        try {
            String dataFromS3Obj = s3Client.getObjectAsString(bucketDirLocation, reportFileName);
            log.info("dataFromS3Obj: " + dataFromS3Obj);
            StringBuilder dataBuilder = new StringBuilder(dataFromS3Obj);
            dataBuilder.append(msgToWrite);
            String dataToWrite = dataBuilder.toString();
            s3Client.putObject(bucketDirLocation, reportFileName, dataToWrite);
        } catch (AmazonS3Exception e) {
            log.error("Error while get object in directory: {} with fileName: {}, with exception {}", bucketDirLocation, reportFileName,
                e.getMessage());
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) { // file not exists
                log.info("Init putting s3Object...");
                try {
                    // Upload a text string as a new object.
                    s3Client.putObject(bucketDirLocation, reportFileName, msgToWrite);
                    log.info("...Finish creating file...");

                } catch (AmazonServiceException ase) {
                    // The call was transmitted successfully, but Amazon S3 couldn't process
                    // it, so it returned an error response.
                    ase.printStackTrace();
                } catch (SdkClientException sdke) {
                    // Amazon S3 couldn't be contacted for a response, or the client
                    // couldn't parse the response from Amazon S3.
                    sdke.printStackTrace();
                }
            }
        }
    }

    // https://stackoverflow.com/questions/28568635/read-aws-s3-file-to-java-code
    @NotNull
    private String buildMessageToWrite(JSONObject jsonObject) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(jsonObject.getString("Timestamp"))
            .append(HYPHEN).append(HYPHEN).append(HYPHEN)
            .append(jsonObject.getString("Message"))
            .append("\n");
        return messageBuilder.toString();
    }

    private String buildReportFileName(String type) {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(now.getYear())
            .append(HYPHEN).append(now.getMonthOfYear())
            .append(HYPHEN).append(now.getDayOfMonth())
            .append(HYPHEN).append(type)
            .append(FileExtension.TXT.getExtension());
        return fileNameBuilder.toString();
    }
}
