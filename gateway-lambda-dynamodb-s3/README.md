# Description:

Integrate Lambda, Dynamodb, S3

**Content**
- [Flow](#flow)
- [Lambdas](#lambdas)
- [S3](#s3)
- [Demo](#demo)

## Flow

![image](https://user-images.githubusercontent.com/37680968/140483674-d2da314c-6a9a-40c1-9b19-d167df4bec72.png)

## Lambdas

### 1. Lambda product upload

This lambda is to upload a product, it integrates with API Gateway

### 2. Lambda product update

This lambda is to update a product, it integrates with API Gateway

### 3. Lambda S3 update

This lambda is triggered when DynamoDB has something changes.

## S3

This is the place where lambda S3 update sends new data and update to a HTML file and it hosts this website as a static website

## Demo

Use Postman to send data

![image](https://user-images.githubusercontent.com/37680968/140485855-5fc82027-eb75-4416-9aea-3ca3dfc786e5.png)

Check DynamoDb

![image](https://user-images.githubusercontent.com/37680968/140489169-fc01f28a-fa9d-4836-be47-0a0c716c8263.png)

Check static website on S3

![image](https://user-images.githubusercontent.com/37680968/140489273-65d18dd2-a41d-4192-bb54-ab2dbde92d85.png)

Update product

![image](https://user-images.githubusercontent.com/37680968/140489458-b3f2f449-8daf-48d7-8c77-b0c8eacc3d05.png)

Check DynamoDb after update

![image](https://user-images.githubusercontent.com/37680968/140489417-17876262-9b64-4339-8fee-6065ca01aff6.png)

Check static website on S3

![image](https://user-images.githubusercontent.com/37680968/140489618-fed79c98-b240-4d88-923a-bc16ad8c4461.png)
