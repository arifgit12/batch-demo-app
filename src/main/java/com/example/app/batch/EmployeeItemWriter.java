package com.example.app.batch;

import com.example.app.entity.Employee;
import com.example.app.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeItemWriter implements ItemWriter<Employee> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeItemWriter.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void write(Chunk<? extends Employee> list) throws Exception {
        logger.info("Writer Thread: {} ", Thread.currentThread().getName());
        employeeRepository.saveAll(list);
    }
}
