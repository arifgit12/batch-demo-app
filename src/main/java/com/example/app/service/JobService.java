package com.example.app.service;

import com.example.app.util.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Value("${source.directory}")
    private String sourceDirectory;

    @Value("${destination.directory}")
    private String destinationDirectory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job job;

    @Autowired
    private NotificationService notificationService;

    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;
    private volatile boolean isStopManually = false;

    // Run every 10 seconds
    @Scheduled(fixedDelay = 10000)
    public void processFiles() {
        if (schedulerEnabled && !isStopManually) {
            logger.info("Starting to process: {}",  LocalDateTime.now());
            startJob();
        }
    }

    @Async
    public CompletableFuture<Boolean> runJobAsync() {
        try {
            isStopManually = false;
            startJob();
            Long processId = getRunningJob();
            if (processId != null) {
                return CompletableFuture.completedFuture(true);
            } else {
                return CompletableFuture.completedFuture(false);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public void startJob(){
        try {
            logger.info("Source Directory {}", sourceDirectory);
            File file = FileHandler.getFile(sourceDirectory);
            if (file != null) {
                logger.info("File : {}", file.getName());
                Long executionId = getRunningJob();
                if (executionId == null) {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("fullPathFileName", file.getAbsolutePath())
                            .addString("destinationDirectory", destinationDirectory)
                            .addLong("startAt", System.currentTimeMillis())
                            .toJobParameters();

                    JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                } else {
                    logger.info("Job is Already Running ProcessId: {}", executionId);
                }
            } else  {
                logger.info("No File Found");
            }
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                JobParametersInvalidException | JobRestartException e) {
            logger.error(e.getMessage());
        }
    }

    public boolean stopJob(){
        try {
            Long executionId = getRunningJob();
            if (executionId != null) {
                jobOperator.stop(executionId);
                logger.info("Stopped job with name: {}", job.getName());
                isStopManually = true;
                return true;
            } else {
                logger.info("No running job found with name found");
            }
        } catch (NullPointerException | NoSuchJobExecutionException
                | JobExecutionNotRunningException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public String getJobStatus() {
        Long status = getRunningJob();
        if (status == null)
            return "Job Not Running";
        else
            return "Job: " + status + " is running";
    }

    private Long getRunningJob() {
        try {
            return jobOperator.getRunningExecutions(job.getName())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (NoSuchJobException e) {
            return null;
        }

    }

    public void enableScheduler() {
        logger.info("Enabling the Scheduler");
        schedulerEnabled = true;
    }

    public void disableScheduler() throws InterruptedException {
        logger.info("Disabling the Scheduler");
        notificationService.notifyUser("Scheduler Disabled");
        schedulerEnabled = false;
    }
}
