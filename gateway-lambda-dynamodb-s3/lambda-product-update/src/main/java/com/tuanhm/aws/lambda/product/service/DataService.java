package com.tuanhm.aws.lambda.product.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.tuanhm.aws.lambda.product.dto.ProductRequest;

public class DataService {

    private static final String DYNAMODB_TBL_NAME = "tbl_product";

    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

    private DynamoDB dynamoDb = new DynamoDB(client);

    public void updateProduct(ProductRequest req, LambdaLogger logger) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("id", req.getId())
            .withUpdateExpression("set #n = :n, price =:p, imgUrl = :iu")
            .withNameMap(new NameMap().with("#n", "name")) // name is a special case for DynamoDb
            .withValueMap(new ValueMap().withString(":n", req.getName())
                .withNumber(":p", req.getPrice())
                .withString(":iu", req.getImgUrl()))
            .withReturnValues(ReturnValue.UPDATED_NEW);

        UpdateItemOutcome outcome = dynamoDb.getTable(DYNAMODB_TBL_NAME).updateItem(updateItemSpec);

        logger.log("Finish updating...");

        if (outcome.getItem() != null) {
            logger.log(outcome.getItem().toJSONPretty());
        } else {
            logger.log("outcome is null");
        }
    }

}
