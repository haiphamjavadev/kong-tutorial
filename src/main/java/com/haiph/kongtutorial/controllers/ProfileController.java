package com.haiph.kongtutorial.controllers;

import com.haiph.kongtutorial.config.Traced;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Profile API", description = "API for user profile operations")
@Slf4j
public class ProfileController {

    @Operation(summary = "Get my profile", description = "Retrieve the profile information of the authenticated user.")
    @GetMapping("/profile")
    public Object profile(HttpServletRequest request) {
        Map<String, Object> xUserInfo = (Map<String, Object>) request.getAttribute("userInfo");
        log.info("hihi: {}", xUserInfo);
        return Map.of(
                "fromHeader", xUserInfo
        );
    }

}
