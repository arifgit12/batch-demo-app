package com.example.app.batch;

import com.example.app.entity.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Profile;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    private String profile;

    @Override
    public Employee process(Employee customer) throws Exception {
        Employee filterCustomer = getValidCustomer(customer);
        return filterCustomer;
    }

    private Employee getValidCustomer(Employee customer) throws EmployeeProcessingException {

        if(isDevProfileActive() && Math.random() < .05) {
            throw new EmployeeProcessingException();
        }

        return customer;
    }

    @Profile("dev")
    private boolean isDevProfileActive() {
        return true;
    }
}
