package org.acme.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter @ToString
public class Employee {
    @Id
    private Integer empId;
    private String empName;
}

