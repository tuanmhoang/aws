## Description: 

Create an order application to practice with SQS, SNS.

## Flow:

![image](https://user-images.githubusercontent.com/37680968/139206379-0ccf0e5c-d9c4-415e-aa5d-12df3c294cdb.png)

## Queues: 
* **order-q**: to store raw data from Application 1
* **accepted-order-q**: result from Application 2, subscribe to topic accepted
* **rejected-order-q**: result from Application 2, subscribe to topic rejected
* **order-accepted-notification-q**: subscribes to what changes in Amazon S3 related to accepted
* **order-rejected-notification-q**: subscribes to what changes in Amazon S3 related to rejected

## Applications:

### 1. Client
This is a simple Reactjs client, which helps to make an order
* Type of goods is liquid or countable items
* Send total order to Application 1 via REST API

### 2. Application 1 (order-service-rest)
This is a REST application, which handles the requests from Client and sends to **order-q**

### 3. Application 2 (process-service)
This application is a Springboot application using `awaitility`.

Reference: https://spring.io/guides/gs/scheduling-tasks/

This application gets the messages from **order-q** and analyzes each item if the order is more than a threshold or not.
* If the order number is more than N number, it is rejected
* If the order number is less than N number, it is accepted

### 4. Application 3 (log-service)
This is also a Springboot app using `awaitility`. 

What is does is to get the messages from **accepted-order-q** and **rejected-order-q** and it writes a report txt file in S3. 

### 5. Application 4 (notification-service)
This is also a Springboot app using `awaitility`.

What it does is to get the messages from **order-accepted-notification-q** and **order-rejected-notification-q** and send data to SNS with a `notification` topic

To alert using email, there are some solutions:
* using SES
* send email to SNS, and create an email subscription on the specific topic
