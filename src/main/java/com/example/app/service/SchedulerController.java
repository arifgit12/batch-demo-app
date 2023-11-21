package com.example.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @Autowired
    private JobService jobService;

    @PostMapping("/enable")
    public ResponseEntity<String> enableScheduler() {
        jobService.enableScheduler();
        return ResponseEntity.ok("Schedular Enabled Successfully");
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disableScheduler() throws InterruptedException {
        jobService.disableScheduler();
        return ResponseEntity.ok("Schedular Disabled Successfully");
    }
}
