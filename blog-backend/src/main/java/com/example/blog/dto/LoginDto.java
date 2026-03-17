package com.example.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {
    @NotBlank(message = "Username or Email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
