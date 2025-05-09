package com.qaisarabbas.coregateway.controllers;

import com.qaisarabbas.coregateway.model.ApiResponse;
import com.qaisarabbas.coregateway.model.LoginRequest;
import com.qaisarabbas.coregateway.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("generate-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateToken(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.generateToken(request));
    }
}
