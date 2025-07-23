package com.example.login2.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://my-login-frontend-bucket.s3-website.ap-northeast-2.amazonaws.com")
@RestController
public class TestCorsController {

    @GetMapping("/")
    public String testCors() {
        return "CORS test successful!";
    }
}