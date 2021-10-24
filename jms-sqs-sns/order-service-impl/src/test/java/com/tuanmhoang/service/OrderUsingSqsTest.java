package com.tuanmhoang.service;

import org.junit.jupiter.api.Test;

import com.tuanmhoang.order.service.impl.OrderUsingSqs;

public class OrderUsingSqsTest {
	
	private OrderUsingSqs orderService = new OrderUsingSqs();
	
	private OrderItemTestUtils orderUtils = new OrderItemTestUtils();

	@Test
	void processShouldSuccess() throws Exception {
//		OrderedItem mockOrderedItems = orderUtils.createMockOrderedItems();
//		orderService.process(mockOrderedItems);
	}
}
