package com.tuanmhoang.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/check")
@Api(tags = "To Check")
public class CheckController {
	@GetMapping
    @ApiOperation(value = "Check if services is up")
    public ResponseEntity<String> checkService() {
        return ResponseEntity.ok("Service is UP");
}
}
