package com.example.app.listener;

import com.example.app.util.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

import java.io.File;

public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger =
            LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job execution started, ProcessId: {}", jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("!!! JOB FINISHED! Verify the results");
            JobParameters jobParameters = jobExecution.getJobParameters();
            if (!jobParameters.isEmpty()) {

                String fullPathFileName = jobParameters.getString("fullPathFileName");
                String destinationDirectory = jobParameters.getString("destinationDirectory");
//                logger.info("Full File Path: {}", fullPathFileName);
//                logger.info("Destination Folder: {}",destinationDirectory);
                if (fullPathFileName != null && destinationDirectory != null) {
                    File file = new File(fullPathFileName);
                    FileHandler.moveFile(file, new File(destinationDirectory, file.getName()));
                    logger.info("File moved from {} to Folder {}", fullPathFileName, destinationDirectory);
                } else {
                    logger.info("File Not Found");
                }
            }
        }
    }
}
