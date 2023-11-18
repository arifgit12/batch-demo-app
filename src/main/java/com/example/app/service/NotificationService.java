package com.example.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async("asyncTaskExecutor")
    public void notifyUser(String notification) throws InterruptedException {
        log.info("Notification {}", notification);
        Thread.sleep(4000L);
        log.info("Notified to the User " + Thread.currentThread().getName());
    }
}
