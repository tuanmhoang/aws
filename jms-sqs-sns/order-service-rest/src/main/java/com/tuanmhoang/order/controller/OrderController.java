package com.tuanmhoang.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuanmhoang.order.dtos.OrderedItem;
import com.tuanmhoang.order.service.OrderService;

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
	
	private final OrderService orderService;
	
	@Autowired
	public OrderController(OrderService order) {
		this.orderService = order;
	}

	@PostMapping
	public ResponseEntity<String> order(@RequestBody List<OrderedItem> orderedItems) {
		orderService.process(orderedItems);
		return ResponseEntity.ok("order successfully");
	}

	
}
