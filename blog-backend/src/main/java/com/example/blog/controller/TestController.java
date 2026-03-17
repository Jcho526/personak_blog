package com.example.blog.controller;

import com.example.blog.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public Result<String> test() {
        return Result.success("Hello, Blog API!");
    }
}