package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EMPLOYEES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Setter(AccessLevel.NONE)
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
}
