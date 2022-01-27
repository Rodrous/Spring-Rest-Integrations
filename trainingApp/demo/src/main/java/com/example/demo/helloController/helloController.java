package com.example.demo.helloController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloController {
    @GetMapping
    public String hello(){
        return "Hello World!";
    }
}
