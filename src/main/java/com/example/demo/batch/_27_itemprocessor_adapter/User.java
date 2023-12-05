package com.example.demo.batch._27_itemprocessor_adapter;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class User {
    private Long id;
    @NotBlank(message = "用户名不能为null或空串")
    private String name;
    private int age;
}
