Create demo applications that implement the following order processing flow. 

First application read the following order details from console and send it to the order queue (orders):

    The user who makes the order;
    Type of goods for the order as liquids or countable item;
    The volume of order for liquids;
    Number of items for countable items;
    Order total.

Second application should notification orders with the following rules:

    If order total greater than some threshold - order should be rejected;
    If already ordered more than N liters - the order should be rejected;
    Summary information for accepted and rejected logs should be passed to other queues or topics.

Third application to log summary about accepted and rejected orders into some file.

    Use message selectors to split orders for liquids and countable items
  
Use topics to implement message exchange
   
Add trigger to S3 bucket that will send message to SQS that file was changed.