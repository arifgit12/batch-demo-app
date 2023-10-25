package com.example.app.service;

import com.example.app.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface EmployeeService {

    Employee createEmployee(Employee employee);

    Employee getEmployeeById(Long id);

    Page<Employee> getAllEmployees(Pageable pageable);

    void updateEmployee(Employee employee);

    void deleteEmployee(Long id);
}

