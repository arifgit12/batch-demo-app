package com.example.app.controller;

import com.example.app.entity.Employee;
import com.example.app.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping
    public Page<Employee> getAllEmployees(@RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        logger.info("Page {} and Size {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.getAllEmployees(pageable);
    }

    @PutMapping("/{id}")
    public void updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = employeeService.getEmployeeById(id);
        if (existingEmployee != null) {
            employee.setId(id);
            employeeService.updateEmployee(employee);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
