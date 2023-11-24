package com.example.app.controller;

import com.example.app.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @Async
    @PostMapping("/start")
    public CompletableFuture<ResponseEntity<String>> startJob() {
        logger.info("Start Jobs");

        try {
            CompletableFuture<Boolean> jobStatus = jobService.runJobAsync();
            //boolean success = jobStatus.join();

            return CompletableFuture.completedFuture(ResponseEntity.ok("Job started successfully."));

        } catch (Exception e) {
            Throwable cause = e.getCause();

            if (cause instanceof InterruptedException) {
                // Preserve the interrupt status and possibly handle it here.
                Thread.currentThread().interrupt();
            }
            logger.error(cause.getMessage());
            ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Job did not start successfully: " + cause.getMessage());
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

    @PostMapping("/status")
    public ResponseEntity<String> statusJob() {
        String status = jobService.getJobStatus();
        return ResponseEntity.ok(status);
    }

    @PostMapping(path = "/importData")
    public ResponseEntity<Boolean> importFile(@RequestParam("file") MultipartFile multipartFile) {
        boolean copiedSuccessfull = jobService.copyFileToSource(multipartFile);
        return ResponseEntity.ok(copiedSuccessfull);
    }
}
