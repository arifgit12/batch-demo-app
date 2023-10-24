package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMPLOYEES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "LASTNAME")
    private String lastName;
    @Column(name = "EMAIL")
    private String email;

    public Long getId() {
        return id;
    }
}
