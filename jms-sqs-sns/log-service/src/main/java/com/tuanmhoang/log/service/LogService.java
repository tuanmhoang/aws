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
import com.tuanmhoang.log.enums.FileExtension;
import com.tuanmhoang.log.enums.ProcessedOrderType;
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

    @Value("${sqs.queue.order.accepted.name:accepted-order-q}")
    private String acceptedQueueName;

    @Value("${sqs.queue.order.rejected.name:rejected-order-q}")
    private String rejectedQueueName;

    @Value("${s3.bucket.order.name:items-order-bucket}")
    private String bucketName;

    @Value("${s3.directory.accepted.name:accepted}")
    private String acceptedDir;

    @Value("${s3.directory.rejected.name:rejected}")
    private String rejectedDir;

    private String bucketDirLocation;

    private String reportFileSuffix;

    public void checkForQueueAndProcess() {
        log.info("receiving messages from queue");

        String acceptedQueueUrl = sqsClient.getQueueUrl(acceptedQueueName).getQueueUrl();
        var acceptedMessages = sqsClient.receiveMessage(acceptedQueueUrl).getMessages();

        String rejectedQueueUrl = sqsClient.getQueueUrl(rejectedQueueName).getQueueUrl();
        var rejectedMessages = sqsClient.receiveMessage(rejectedQueueUrl).getMessages();

        // check for accepted messages
        if (CollectionUtils.isEmpty(acceptedMessages)) {
            log.info("There is no new message from {}", acceptedQueueName);
        } else {
            log.info("Processing data from {}", acceptedQueueName);
            this.withProcessedOrderType(ProcessedOrderType.ACCEPTED)
                .processMessages(acceptedQueueUrl, acceptedMessages);
        }

        // check for rejected messages
        if (CollectionUtils.isEmpty(rejectedMessages)) {
            log.info("There is no new message from {}", rejectedQueueName);
        } else {
            log.info("Processing data from {}", rejectedQueueName);
            this.withProcessedOrderType(ProcessedOrderType.REJECTED)
                .processMessages(rejectedQueueUrl, rejectedMessages);
        }
    }

    private void processMessages(String queueUrl, List<Message> messages) {
        List<String> messagesFromQueue = messages.stream()
            .map(Message::getBody)
            .collect(Collectors.toList());
        log.info("Processing messagesFromAcceptedQueue...");
        processMessagesFromQueue(messagesFromQueue);
        log.info("Done processing messagesFromAcceptedQueue - Deleting messages...");
        for (Message m : messages) {
            DeleteMessageResult deleteMessageResult = sqsClient.deleteMessage(queueUrl, m.getReceiptHandle());
            int statusCode = deleteMessageResult.getSdkHttpMetadata().getHttpStatusCode();
            log.info("Deleted message << {} >> with statusCode: {} ", m.getBody(), statusCode);
        }
    }

    private LogService withProcessedOrderType(ProcessedOrderType type) {
        String processedTypeName = type.getProcessedType();
        this.bucketDirLocation = bucketName + "/" + processedTypeName;
        this.reportFileSuffix = processedTypeName;
        return this;
    }

    private void processMessagesFromQueue(List<String> messagesFromQueue) {
        // build file name
        String reportFileName = buildReportFileName(this.reportFileSuffix);
        // build message to write
        StringBuilder msgToWriteBuilder = new StringBuilder();
        for (String msg : messagesFromQueue) {
            //log.info("Processing message: {}", msg);
            JSONObject jsonObject = new JSONObject(msg);
            msgToWriteBuilder.append(buildMessageToWrite(jsonObject));
        }
        String msgToWrite = msgToWriteBuilder.toString();
        log.info("msgToWrite : {}", msgToWrite);

        try {
            var dataFromS3Obj = s3Client.getObjectAsString(bucketDirLocation, reportFileName);
            log.info("dataFromS3Obj: {}", dataFromS3Obj);
            StringBuilder dataBuilder = new StringBuilder(dataFromS3Obj);
            dataBuilder.append(msgToWrite);
            s3Client.putObject(bucketDirLocation,
                reportFileName,
                dataBuilder.toString()
            );

        } catch (AmazonS3Exception e) {
            log.error("Error while get object in directory: {} with fileName: {}, with exception {}",
                bucketDirLocation,
                reportFileName,
                e.getMessage()
            );
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) { // file not exists
                log.info("Init putting s3Object...");
                try {
                    // Upload a text string as a new object.
                    s3Client.putObject(bucketDirLocation, reportFileName, msgToWrite);
                    log.info("...Finish creating file...");

                } catch (AmazonServiceException ase) {
                    // The call was transmitted successfully, but Amazon S3 couldn't process
                    // it, so it returned an error response.
                    log.error("Error while process S3",
                        ase);
                } catch (SdkClientException sdke) {
                    // Amazon S3 couldn't be contacted for a response, or the client
                    // couldn't parse the response from Amazon S3.
                    log.error("Error while process S3",
                        sdke);
                }
            }
        }
    }

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
