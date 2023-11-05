package com.example.app.config;

import com.example.app.batch.EmployeeItemWriter;
import com.example.app.batch.EmployeeProcessor;
import com.example.app.batch.ExceptionSkipPolicy;
import com.example.app.listener.JobCompletionNotificationListener;
import com.example.app.entity.Employee;
import com.example.app.listener.StepSkipListener;
import com.example.app.partition.ColumnRangePartitioner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
public class BatchConfig {

    @Autowired
    private EmployeeItemWriter employeeItemWriter;

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader(@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
        FlatFileItemReader<Employee> flatFileItemReader  = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(new File(pathToFile)));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader .setLinesToSkip(1);
        flatFileItemReader .setLineMapper(lineMapper());
        flatFileItemReader .close();
        return flatFileItemReader;
    }

    private LineMapper<Employee> lineMapper() {
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name", "lastName", "email");

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    @Bean
    public ColumnRangePartitioner partitioner() {
        return new ColumnRangePartitioner();
    }

    @Bean
    public PartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler
                taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(4);

        // Set a task executor for parallel execution of partitions
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());

        taskExecutorPartitionHandler.setStep(slaveStep(null,null));
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step slaveStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-slaveStep", jobRepository)
                .<Employee, Employee>chunk(250, transactionManager)
                .reader(reader(null))
                .processor(processor())
                .writer(employeeItemWriter)
                .faultTolerant()
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .build();
    }

    @Bean
    public Step masterStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-masterStep", jobRepository)
                    .partitioner(
                            slaveStep(null, null).getName(),partitioner())
                    .partitionHandler(partitionHandler())
                    .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {
        return new JobBuilder("employeeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobCompletionNotificationListener())
                .flow(masterStep(null,null))
                .end()
                .build();
    }

    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    @Bean
    public SkipListener skipListener() {
        return new StepSkipListener();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(4);
        asyncTaskExecutor.setCorePoolSize(4);
        asyncTaskExecutor.setQueueCapacity(4);
        return asyncTaskExecutor;
    }
}
