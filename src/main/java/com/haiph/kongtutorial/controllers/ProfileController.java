package com.haiph.kongtutorial.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public Object profile(HttpServletRequest request) {
        Map<String, Object> xUserInfo = (Map<String, Object>) request.getAttribute("userInfo");
        return Map.of(
                "fromHeader", xUserInfo
        );
    }

}
