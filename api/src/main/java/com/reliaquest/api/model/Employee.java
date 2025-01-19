package com.reliaquest.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "employee_details")
public class Employee {

    @Id
    UUID id;
    String name;
    Integer salary;
    Integer age;
    String title;
    String email;

    public Employee(UUID id, String name, int salary, int age, String title, String email) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
        this.email = email;
    }
}
