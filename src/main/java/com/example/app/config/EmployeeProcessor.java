package com.example.app.config;

import com.example.app.entity.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee customer) throws Exception {
        return customer;
    }
}
