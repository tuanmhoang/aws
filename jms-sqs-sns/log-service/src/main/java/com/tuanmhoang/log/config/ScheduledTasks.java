package com.tuanmhoang.log.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tuanmhoang.log.service.LogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final LogService logService;
	
	@Autowired
	public ScheduledTasks(LogService logService) {
		this.logService = logService;
	}

	@Scheduled(fixedRate = 20000000)
	public void cronJobToCheckOrderQueue() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		logService.checkForQueueAndProcess();
	}
}
