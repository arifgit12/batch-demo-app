package com.example.app.controller;

import com.example.app.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @PostMapping("/start")
    public CompletableFuture<ResponseEntity<String>> startJob() {
        logger.info("Start Jobs");

        try {
            CompletableFuture<Boolean> jobExecution = jobService.runJobAsync();
            return CompletableFuture.completedFuture(ResponseEntity.ok("Job started successfully."));
        } catch (Exception e) {
            logger.error(e.getMessage());
            ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Job did not start successfully: " + e.getMessage());
            return CompletableFuture.completedFuture(result);
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopJob() {
        if (jobService.stopJob()) {
            return ResponseEntity.ok("Job Stopped successfully.");
        } else {
            return ResponseEntity.ok("Job Failed to Stop.");
        }
    }

}
