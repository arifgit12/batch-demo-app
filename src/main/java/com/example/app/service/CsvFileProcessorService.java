package com.example.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsvFileProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileProcessorService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public JobExecution processCsvFile(String filePath) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("csvResource", "file:" + filePath)
                    .addLong("time",System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            logger.info(jobExecution.getStatus().name() + " " + jobExecution.getStatus());

            return jobExecution;
        } catch (JobInstanceAlreadyCompleteException | JobParametersInvalidException |
                    JobExecutionAlreadyRunningException |
                JobRestartException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }
}

