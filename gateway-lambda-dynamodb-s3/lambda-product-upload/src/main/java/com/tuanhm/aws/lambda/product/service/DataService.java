package com.tuanhm.aws.lambda.product.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.tuanhm.aws.lambda.product.dto.ProductRequest;
import java.util.UUID;

public class DataService {

    private static final String DYNAMODB_TBL_NAME = "tbl_product";

    //private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

    private DynamoDB dynamoDb = initDynamoDbClient();

    public void uploadProduct(ProductRequest req, LambdaLogger logger) {
        Item itemToUpload = new Item().withString("id", generateUniqueId())
            .withString("name", req.getName())
            .withNumber("price", req.getPrice())
            .withString("picture_url", req.getImgUrl());
        PutItemOutcome putItem = dynamoDb.getTable(DYNAMODB_TBL_NAME)
            .putItem(new PutItemSpec()
                .withItem(itemToUpload)
            );

        logger.log("Finish uploading...");
        if (putItem.getItem() != null) {
            logger.log(putItem.getItem().toJSONPretty());
        } else {
            logger.log("putItem is null");
        }
        logger.log("Saved in db");
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }


    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .build();

        return new DynamoDB(client);
    }

}
