//package com.example.app.config;
//
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.partition.PartitionHandler;
//import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BatchPartitionConfig {
//
//    @Bean
//    public PartitionHandler partitionHandler() {
//        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
//        taskExecutorPartitionHandler.setGridSize(4);
//        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
//        taskExecutorPartitionHandler.setStep(slaveStep());
//        return taskExecutorPartitionHandler;
//    }
//
//    @Bean
//    public Step slaveStep() {
//        return stepBuilderFactory.get("slaveStep").<Customer, Customer>chunk(250)
//                .reader(reader())
//                .processor(processor())
//                .writer(customerWriter)
//                .build();
//    }
//
//    @Bean
//    public Step masterStep() {
//        return stepBuilderFactory.get("masterSTep").
//                partitioner(slaveStep().getName(), partitioner())
//                .partitionHandler(partitionHandler())
//                .build();
//    }
//}
