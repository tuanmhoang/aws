package com.tuanmhoang.process.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tuanmhoang.process.service.ProcessOrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final ProcessOrderService processService;
	
	@Autowired
	public ScheduledTasks(ProcessOrderService processService) {
		this.processService = processService;
	}

	@Scheduled(fixedRate = 20000)
	public void cronJobToCheckOrderQueue() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		processService.checkForQueueAndProcess();
	}
}
