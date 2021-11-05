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

    private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    private DynamoDBMapper mapper = new DynamoDBMapper(client);

    public List<Product> getProducts(LambdaLogger logger) {
        logger.log("Loading db...");
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Product> scanResult = mapper.scan(Product.class, scanExpression);
        logger.log("There are " + scanResult.size() + " items found.");
        return scanResult;
    }
}
