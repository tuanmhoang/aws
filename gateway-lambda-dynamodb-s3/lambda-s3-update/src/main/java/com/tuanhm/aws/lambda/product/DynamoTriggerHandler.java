package com.tuanhm.aws.lambda.product;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.tuanhm.aws.lambda.product.service.DataService;

public class DynamoTriggerHandler implements RequestHandler<DynamodbEvent, String> {

    private DataService dataService = new DataService();

    @Override
    public String handleRequest(DynamodbEvent ddbEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("getting data... newer222");
//        for (DynamodbStreamRecord record : ddbEvent.getRecords()) {
//            logger.log("record.getEventID(): " + record.getEventID());
//            logger.log("record.getEventName(): " + record.getEventName());
//            logger.log("record.getDynamodb().toString(): " + record.getDynamodb().toString());
//        }
        // get data
        dataService.getItems(logger);

        // build html page

        // save to s3

        return "Successfully processed " + ddbEvent.getRecords().size() + " records.";
    }
}
//    @Override
//    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
//        LambdaLogger logger = context.getLogger();
//        logger.log("hello world");
//    }

