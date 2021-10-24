package com.tuanmhoang.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/check")
public class CheckController {
	@GetMapping
    public ResponseEntity<String> checkService() {
        return ResponseEntity.ok("Service is UP");
}
}
