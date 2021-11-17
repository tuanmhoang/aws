package com.tuanhm.aws.lambda.product;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tuanhm.aws.lambda.product.entity.Product;
import com.tuanhm.aws.lambda.product.service.DataService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DynamoTriggerHandler implements RequestHandler<DynamodbEvent, String> {

    private DataService dataService = new DataService();

    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    private static final String BUCKET_DIR_LOCATION = "tuanmhoangproducts";

    private static final String HTML_FILE_NAME = "index.html";

    @Override
    public String handleRequest(DynamodbEvent ddbEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("getting data... ");
        // get data
        List<Product> products = dataService.getProducts(logger);

        // build html page
        String htmlContentAsString = null;
        try {
            htmlContentAsString = buildHtmlAsString(products);
        } catch (IOException e) {
            logger.log("Error while create the message: " + e);
        }
        logger.log("Message built: " + htmlContentAsString);

        // save to s3
        saveHtmlFileToS3(htmlContentAsString, logger);

        return "Successfully processed " + ddbEvent.getRecords().size() + " records.";
    }

    private void saveHtmlFileToS3(String htmlContentAsString, LambdaLogger logger) {
        try (InputStream inputStream = IOUtils.toInputStream(htmlContentAsString, StandardCharsets.UTF_8)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/html");
            metadata.addUserMetadata("title", "Product data");
            metadata.setContentLength(htmlContentAsString.length());
            s3Client.putObject(BUCKET_DIR_LOCATION,
                HTML_FILE_NAME,
                inputStream,
                metadata
            );
        } catch (AmazonS3Exception | IOException e) {
            logger.log(String.format("Error while get object in directory: %s with fileName: %s, with exception %s",
                BUCKET_DIR_LOCATION,
                HTML_FILE_NAME,
                e.getMessage())
            );
        }
    }

    private String buildHtmlAsString(List<Product> products) throws IOException {
        File htmlTemplateFile = new File("template.html");
        String htmlString = FileUtils.readFileToString(htmlTemplateFile, StandardCharsets.UTF_8.name());
        StringBuilder dataBuilder = new StringBuilder();
        products.forEach(p -> {
            dataBuilder.append("<tr>")
                .append("<td>").append(p.getId()).append("</td>")
                .append("<td>").append(p.getName()).append("</td>")
                .append("<td>").append(p.getPrice()).append("</td>")
                .append("<td>").append(p.getImgUrl()).append("</td>")
                .append("</tr>");
        });
        return htmlString.replace("$data", dataBuilder.toString());
    }
}
