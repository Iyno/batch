package com.example.demo.batch._30_itemprocessor_customize;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class User {
    private Long id;

    private String name;
    private int age;
}
