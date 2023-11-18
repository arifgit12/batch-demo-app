package com.example.app.batch;

import com.example.app.entity.Employee;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class EmloyeeFieldSetMapper implements FieldSetMapper<Employee> {

    @Override
    public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
        Employee employee = new Employee();
        employee.setName(fieldSet.readString("name"));
        employee.setLastName(fieldSet.readString("lastName"));
        employee.setEmail(fieldSet.readString("email"));
        return employee;
    }
}
