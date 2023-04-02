package com.tuanmhoang.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.tuanmhoang.aws.exceptions.ApiCallException;
import com.tuanmhoang.aws.model.SqsMessage;

public class SqsCallApiHandler implements RequestHandler<SQSEvent, Void> {

    private Gson gson = new Gson();

    // these following configs can also be refactored and configured using lambda env.
    private static final String DYNAMODB_TBL_NAME = "tbl_users";

    private static final String PRIMARY_KEY = "user_id";

    private static final String SPECIAL_USER_ID = "1234abcd";

    private static final String API_CALL_QUEUE = "api-call-sqs";

    private static final String API_CALL_DLQ_QUEUE = "api-call-dlq-sqs";

    private AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .build();

    private DynamoDB dynamoDb = new DynamoDB(AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .build());

    public Void handleRequest(SQSEvent event, Context context) {
        System.out.println("lambda is running...");

        // receive message from sqs
        SqsMessage receivedMsg = null;
        System.out.println("Records size is: " + event.getRecords().size());
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            String msgBody = msg.getBody();
            System.out.println(msgBody);
            receivedMsg = gson.fromJson(msgBody, SqsMessage.class);
        }

        // call API part
        try {
            System.out.println("Doing call3rdPartyApi...");
            call3rdPartyApi(receivedMsg);
        } catch (ApiCallException ex) {
            // handle response, failed case send to SQS
            System.out.println("Doing handleFailedApiCallCase...");
            handleFailedApiCallCase(receivedMsg);
        }

        return null;
    }

    /**
     * <pre>
     * if attempts = 3, send to DLQ, else send to queue and increase attempts.
     *
     * @param receivedMsg
     * </pre>
     */
    private void handleFailedApiCallCase(SqsMessage receivedMsg) {
        int attempt = receivedMsg.getAttempts();
        System.out.println("Current attempts is: " + attempt);
        if (attempt == 3) {
            System.out.println("Sending to DLQ");
            sendSqs(gson.toJson(receivedMsg), API_CALL_DLQ_QUEUE);
        } else {
            System.out.println("Sending to call API queue");
            receivedMsg.setAttempts(receivedMsg.getAttempts() + 1);
            sendSqs(gson.toJson(receivedMsg), API_CALL_QUEUE);
        }
    }

    /**
     * <pre>
     * here we do a trick for quick demo.
     * if user_id = SPECIAL_USER_ID, then create mock data and save to Dynamo, else send back to SQS to do retry
     * @param receivedMsg
     * </pre>
     */

    private void call3rdPartyApi(SqsMessage receivedMsg) {
        String userId = receivedMsg.getUserId();
        System.out.println("call3rdPartyApi - userId: " + userId);
        // in real case, we can use feign or whatever.
        if (SPECIAL_USER_ID.equals(userId)) {
            // create mock data and save to Dynamo
            handleSuccessfulCase();
        } else {
            // send to SQS
            throw new ApiCallException("Cannot call API");
        }
    }

    private void handleSuccessfulCase() {
        // create mock data and save to dynamodb
        Item itemToUpload = new Item()
                .withPrimaryKey(PRIMARY_KEY, SPECIAL_USER_ID)
                .withString("name", "Bob")
                .withNumber("age", 32);

        Table tbl = dynamoDb.getTable(DYNAMODB_TBL_NAME);
        PutItemOutcome putItem = tbl.putItem(itemToUpload);
        System.out.println("Finish uploading...");
        if (putItem.getItem() != null) {
            System.out.println(putItem.getItem().toJSONPretty());
        } else {
            System.out.println("putItem is null");
        }
        System.out.println("Saved in db!");
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
