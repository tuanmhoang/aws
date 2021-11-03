package com.tuanhm.aws.lambda.product;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuanhm.aws.lambda.product.dto.ProductRequest;
import com.tuanhm.aws.lambda.product.dto.ProductResponse;
import com.tuanhm.aws.lambda.product.service.DataService;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UploadProductHandler implements RequestStreamHandler {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final DataService dataService = new DataService();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        JSONObject responseJson = new JSONObject();

        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        logger.log("Parsing data... ");
        try {

            JSONObject event = (JSONObject) parser.parse(reader);
            if (event.get("body") != null) {
                String body = (String) event.get("body");
                logger.log("Message body: " + body);
                ProductRequest req = gson.fromJson(body, ProductRequest.class);
                dataService.uploadProduct(req, logger);

                JSONObject responseBody = new JSONObject();
                responseBody.put("message", "Saved successfully!!!");

                JSONObject headerJson = new JSONObject();
                headerJson.put("Access-Control-Allow-Origin", "*");
                headerJson.put("Access-Control-Allow-Methods", "POST");
                headerJson.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");

                responseJson.put("statusCode", 200);
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody.toString());
            }
        } catch (Exception e) {
            responseJson.put("statusCode", 400);
            responseJson.put("exception", e);
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
}
