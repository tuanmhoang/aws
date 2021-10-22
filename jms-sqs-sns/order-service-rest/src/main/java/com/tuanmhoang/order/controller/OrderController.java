package com.tuanmhoang.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuanmhoang.dtos.OrderedItem;
import com.tuanmhoang.order.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/order")
@Api(tags = "Order items")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
	
	private final OrderService orderService;
	
	@Autowired
	public OrderController(OrderService order) {
		this.orderService = order;
	}

	@PostMapping()
	@ApiOperation(value = "Order items")
	public ResponseEntity<String> order(@RequestBody List<OrderedItem> orderedItems) {
		orderService.process(orderedItems);
		return ResponseEntity.ok("order successfully");
	}

}
