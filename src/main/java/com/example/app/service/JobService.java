package com.example.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private JobOperator jobOperator;

    @Async
    public CompletableFuture<JobExecution> runJobAsync() {
        try {
            Long executionId = getRunningJob();
            if (executionId == null) {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("startAt", System.currentTimeMillis()).toJobParameters();
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                return CompletableFuture.completedFuture(jobExecution);
            }else {
                return CompletableFuture.completedFuture(null);
            }
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                JobParametersInvalidException | JobRestartException e) {
            // Handle exceptions as needed
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
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

    public boolean stopJob(){
        boolean isJobStopped = false;
        try {
            Long executionId = getRunningJob();
            if (executionId != null) {
                jobOperator.stop(executionId);
                logger.info("Stopped job with name: " + job.getName());
                isJobStopped = true;
            } else {
                logger.info("No running job found with name found");
            }
        } catch (NullPointerException | NoSuchJobExecutionException |
                JobExecutionNotRunningException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
        return isJobStopped;
    }
}
