package com.example.app.service;

import com.example.app.entity.Employee;
import com.example.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = false)
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Transactional(readOnly = false)
    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}

