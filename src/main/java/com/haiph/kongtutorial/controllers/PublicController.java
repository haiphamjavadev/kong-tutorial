package com.haiph.kongtutorial.controllers;

import com.haiph.kongtutorial.config.BaseWebClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public")
public class PublicController {
    private final BaseWebClientService baseWebClientService;

    public PublicController(BaseWebClientService baseWebClientService) {
        this.baseWebClientService = baseWebClientService;
    }

    @GetMapping
    public ResponseEntity<?> getParams(@RequestParam String params) {
        String messageInNotification = baseWebClientService.get("http://localhost:8091/notification/api/test?param=" + params, String.class, "");
        return ResponseEntity.ok("You have sent params: " + messageInNotification);
    }
}
