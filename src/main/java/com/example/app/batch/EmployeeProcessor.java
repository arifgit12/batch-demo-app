package com.example.app.batch;

import com.example.app.entity.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee customer) throws Exception {
        Employee filterCustomer = getValidCustomer(customer);
        return filterCustomer;
    }

    private Employee getValidCustomer(Employee customer) throws EmployeeProcessingException {

        if(Math.random() < .03) {
            throw new EmployeeProcessingException();
        }

        return customer;
    }
}
