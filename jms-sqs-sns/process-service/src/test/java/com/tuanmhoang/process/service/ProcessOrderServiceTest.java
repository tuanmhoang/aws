package com.tuanmhoang.process.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProcessOrderServiceTest {
	
	@Autowired
	private ProcessOrderService service;
	
	@Test
	void processServiceShouldWork() throws Exception {
		service.checkForQueueAndProcess();
	}

}
