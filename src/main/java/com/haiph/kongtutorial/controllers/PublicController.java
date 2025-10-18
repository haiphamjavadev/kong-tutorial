package com.haiph.kongtutorial.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
public class PublicController {

    @GetMapping
    public ResponseEntity<?> getParams (@RequestParam String params) {
        return ResponseEntity.ok("You have sent params: " + params);
    }
}
