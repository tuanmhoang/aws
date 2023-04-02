# Retry solution

## Overview

![image](https://user-images.githubusercontent.com/37680968/229327814-6946c17e-2eca-438a-91dc-8efe325957db.png)

Components:

- `prepare data service`: helps to fetch some data for later usage.
- `api-call-sqs`: helps to trigger `callapi-lambda`
- `api-call-dlq-sqs`: when fail & max attempts reaches
- `callapi-lambda`: helps to call `3rd party`, response data if success save in Dynamo, if not success and not reach match attempts, send message to SQS
- `3rd party`: 3rd party service
- `DynamoDB`: store data

Flows:

- fetch data service check DynamoDB
- in case there is data from DynamoDB, do not need to trigger SQS flow. Skip below steps.
- in case there is no data from DynamoDB, send to SQS `api-call-sqs`

## Setup & Run

1. Create DynamoDb table `tbl_users`

![image](https://user-images.githubusercontent.com/37680968/229327398-e66a42e2-53d2-454f-9182-2fcd401deea8.png)

2. Call API to create dummy user: http://localhost:8080/v1/user/dummy

![image](https://user-images.githubusercontent.com/37680968/229327707-a637e167-41e2-462c-a0b4-f7dfd4288a6f.png)

![image](https://user-images.githubusercontent.com/37680968/229327505-9a8c05fd-2234-420f-a552-ee7a890ee920.png)

3. Call API to run fetch data flow: http://localhost:8080/v1/user/d39151eb-c065-48d7-820f-c91d2abafaed

![image](https://user-images.githubusercontent.com/37680968/229327549-4beab720-64f5-45ed-be6b-428395b3e439.png)

Actually, the purpose is to `prepare data`, which means it checks if DynamoDB has no data then we trigger the process to do the retry, not to `fetch data`, but I still create response like this to test fetching dta from DynamoDB :wink:

4. Call API to fetch not existing data: http://localhost:8080/v1/user/ , this triggers the flow sending SQS and retry

Setup queues

![image](https://user-images.githubusercontent.com/37680968/229328159-08d7ba24-d953-4820-8a54-5fb753b9cd9e.png)

Call API: http://localhost:8080/v1/user/d39151eb-c065-48d7-820f-c91d2abafaee (the ID is the UUID, which is randomly generated)

![image](https://user-images.githubusercontent.com/37680968/229328193-b1128fff-85a3-4304-86fd-2a938f42ddc2.png)

Message available on queue: ![image](https://user-images.githubusercontent.com/37680968/229328243-2ef5ff27-dab7-416c-a256-a17ed3c52984.png)

5. Create lambda to subscribe queue and execute

Add trigger

![image](https://user-images.githubusercontent.com/37680968/229330785-5e06814d-58d9-4c6c-bf06-e789cae927c3.png)

Setup lambda

![image](https://user-images.githubusercontent.com/37680968/229330856-d65f0649-dfde-4c8f-9f41-77e67606a90a.png)

Try to trigger

![image](https://user-images.githubusercontent.com/37680968/229331529-18568ddd-653b-4986-a969-425a6ef69b2f.png)

In lambda, let's hardcode:

- id = 1234abcd: return data, save to DynamoDB
- other ids trigger retry 3 times until the message is sent to DLQ

To call for the return data case, use: http://localhost:8080/v1/user/1234abcd

![image](https://user-images.githubusercontent.com/37680968/229336781-f31a9132-2ccf-4a8d-87d5-5200ccf324ee.png)

Lambda is triggered successfully and check DynamoDb

![image](https://user-images.githubusercontent.com/37680968/229337263-5f8605c9-5956-4306-9317-b91340b04c8b.png)

The hardcoded user for case call API successfully (see `callapi-lambda` implementation)

```
// create mock data and save to dynamodb
Item itemToUpload = new Item()
      .withPrimaryKey(PRIMARY_KEY, SPECIAL_USER_ID)
      .withString("name", "Bob")
      .withNumber("age", 32);
```

Check for other ids to make sure the retry works. Let's try with http://localhost:8080/v1/user/1234abcd123

![image](https://user-images.githubusercontent.com/37680968/229337414-9fdac2c1-d440-4e3f-932d-8783982ec806.png)

Lambda retries 3 times before sending to DLQ

![Screenshot 2023-04-02 at 13 56 08](https://user-images.githubusercontent.com/37680968/229337666-97da600d-60be-4ef3-9570-0be83b1b10eb.png)

There are 5 message in DLQ.

### Notes

Remember to add Policies for lambda to work with SQS and DynamoDb