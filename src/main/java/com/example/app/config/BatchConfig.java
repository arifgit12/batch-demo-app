package com.example.app.config;

import com.example.app.entity.Employee;
import com.example.app.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableAsync
public class BatchConfig {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${csv.file.path}")
    private String csvResource; // Path to the CSV file

    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(csvResource));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("name", "lastName", "email");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Employee.class);
            }});
        }});
        return reader;
    }

    @Bean
    public EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employee> writer() {
        RepositoryItemWriter<Employee> writer = new RepositoryItemWriter<>();
        writer.setRepository(employeeRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step",jobRepository).
                <Employee, Employee>chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importEmployees",jobRepository)
                .flow(step(jobRepository,transactionManager)).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
