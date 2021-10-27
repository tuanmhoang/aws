package notification.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final NotificationService notificationService;
	
	@Autowired
	public ScheduledTasks(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Scheduled(fixedRate = 20000)
	public void cronJobToCheckOrderQueue() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		notificationService.checkForQueueAndProcess();
	}
}
