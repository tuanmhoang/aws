package com.tuanmhoang.service;

import com.tuanmhoang.dtos.OrderedItems;

public interface OrderService {
	/**
	 * process the order items and send to sqs
	 */
	void process(OrderedItems orderedItems);
}
