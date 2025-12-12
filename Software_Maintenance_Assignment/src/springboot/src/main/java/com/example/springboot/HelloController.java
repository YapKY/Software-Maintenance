package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // Removed "/" mapping - now handled by ViewController for serving index.html
    // This was the default Spring Boot hello world endpoint

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World! Spring Boot is running.";
    }

}