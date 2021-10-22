package com.tuanmhoang.order.service;

import java.util.List;

import com.tuanmhoang.dtos.OrderedItem;

public interface OrderService {
	/**
	 * process the order items and send to sqs
	 */
	void process(List<OrderedItem> orderedItems);
}
