package com.tuanhm.aws.lambda.product.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.tuanhm.aws.lambda.product.entity.Product;
import java.util.List;

public class DataService {

    private static final String DYNAMODB_TBL_NAME = "tbl_product";

    //private DynamoDB dynamoDbClient = initDynamoDbClient();

    private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    public List<Product> getItems(LambdaLogger logger) {
        logger.log("Loading db...[0]");
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        logger.log("Loading db...[1]");
        // Change to your Table_Name (you can load dynamically from lambda env as well)
//        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder().withTableNameOverride(
//            DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(DYNAMODB_TBL_NAME)).build();
//
//        DynamoDBMapper mapper = new DynamoDBMapper(client, mapperConfig);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        logger.log("Loading db...[2]");
        // Change to your model class
        List<Product> scanResult = mapper.scan(Product.class, scanExpression);
        logger.log("Loading db...[3]");
        // Check the count and iterate the list and perform as desired.
        scanResult.size();
        logger.log("Loading db...[4]");
        scanResult.forEach(r -> logger.log(r.toString()));

        return null;
    }

    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .build();
        return new DynamoDB(client);
    }

}
