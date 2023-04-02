package com.tuanmhoang.aws.service.impl;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.tuanmhoang.aws.model.ApiCallRequest;
import com.tuanmhoang.aws.model.User;
import com.tuanmhoang.aws.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private Gson gson = new Gson();

    private final AmazonSQS sqs;
    private final DynamoDB dynamoDb;
    private static final String DYNAMODB_TBL_NAME = "tbl_users";

    private static final String PRIMARY_KEY = "user_id";

    @Value("${sqs.apicall.name}")
    private String sqsApiCallName;

    @Override
    public User fetchData(String id) {

        // fetch data from dynamodb
        Table tbl = dynamoDb.getTable(DYNAMODB_TBL_NAME);

        // if exist return data
        Item item = tbl.getItem(PRIMARY_KEY, id);
        if (item != null) {
            return User.builder()
                    .id(item.getString("user_id"))
                    .name(item.getString("name"))
                    .age(item.getInt("age"))
                    .build();
        }

        // if not exist send to sqs
        ApiCallRequest request = new ApiCallRequest();
        request.setAttempts(1);
        request.setUser_id(id);

        String dataToSqs = gson.toJson(request);
        sendSqs(dataToSqs, sqsApiCallName);

        return null;
    }

    @Override
    public void createDummyData() {
        Item itemToUpload = new Item()
                .withPrimaryKey(PRIMARY_KEY, generateUniqueId())
                .withString("name", "Alice")
                .withNumber("age", 25);
        Table tbl = dynamoDb.getTable(DYNAMODB_TBL_NAME);
        PutItemOutcome putItem = tbl.putItem(itemToUpload);
        log.info("Finish uploading...");
        if (putItem.getItem() != null) {
            log.info(putItem.getItem().toJSONPretty());
        } else {
            log.info("putItem is null");
        }
        log.info("Saved in db");
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
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
