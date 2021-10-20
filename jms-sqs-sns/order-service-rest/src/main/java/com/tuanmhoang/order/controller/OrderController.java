package com.tuanmhoang.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuanmhoang.dtos.OrderedItems;
import com.tuanmhoang.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/v1/order")
@Api(tags = "Order items")
public class OrderController {
	
	private final OrderService order;

	@PostMapping
	@ApiOperation(value = "Order items")
	public ResponseEntity<String> order(@RequestBody OrderedItems orderedItems) {
		
		return ResponseEntity.ok("hello world");
	}

}
