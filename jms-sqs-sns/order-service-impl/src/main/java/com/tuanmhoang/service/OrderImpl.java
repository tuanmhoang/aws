package com.tuanmhoang.service;

import com.tuanmhoang.dtos.OrderedItems;

public class OrderImpl implements OrderService{

	@Override
	public void process(OrderedItems orderedItems) {
		System.out.println("sending to sqs");
	}

}
