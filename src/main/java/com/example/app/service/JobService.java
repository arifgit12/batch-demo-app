package com.example.app.service;

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
    private Job job;

    @Autowired
    private JobOperator jobOperator;

    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;

    private volatile boolean isJobRunning = false;

    // Run every 10 seconds
    @Scheduled(fixedDelay = 20000)
    public void processFiles() {
        if (schedulerEnabled && !isJobRunning) {
            logger.info("Starting to process: {}",  LocalDateTime.now());
            startJob();
        }
    }

    @Async
    public CompletableFuture<Boolean> runJobAsync() {
        try {
            boolean jobStarted = startJob();
            return CompletableFuture.completedFuture(jobStarted);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public boolean startJob(){
        isJobRunning = false;
        try {
            logger.info("Source Directory " + sourceDirectory);
            File file = FileMonitoringService.getFile(sourceDirectory);
            if (file != null) {
                logger.info("File : {}", file.getName());
                Long executionId = getRunningJob();
                if (executionId == null) {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("csvResource", "file:" + file.getAbsolutePath())
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

                    JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                    if (jobExecution.isRunning()) {
                        isJobRunning = true;
                    } else {
                        Thread.sleep(1000);
                        FileMonitoringService.moveFile(file, new File(destinationDirectory, file.getName()));
                    }
                }
            } else  {
                logger.info("No File Found");
            }
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                JobParametersInvalidException | JobRestartException | InterruptedException e) {
            logger.error(e.getMessage());
        }
        return isJobRunning;
    }

    public boolean stopJob(){
        boolean isJobStopped = false;
        try {
            Long executionId = getRunningJob();
            if (executionId != null) {
                jobOperator.stop(executionId);
                logger.info("Stopped job with name: {}", job.getName());
                isJobStopped = true;
                isJobRunning = false;
            } else {
                logger.info("No running job found with name found");
            }
        } catch (NullPointerException | NoSuchJobExecutionException |
                JobExecutionNotRunningException e) {
            logger.error(e.getMessage());
        }
        return isJobStopped;
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
}
