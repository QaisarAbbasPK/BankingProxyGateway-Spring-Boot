package com.qaisarabbas.coregateway.controllers;

import com.qaisarabbas.coregateway.model.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class PingController {
    private final String MESSAGE = "Banking Proxy Gateway is up and running date is " + new Date();

    @GetMapping
    public ApiResponse<String> ping() {
        return ApiResponse.success(MESSAGE);
    }

    @PostMapping
    public ApiResponse<String> pingPost() {
        return ApiResponse.success(MESSAGE);
    }

    @DeleteMapping
    public ApiResponse<String> pingDelete() {
        return ApiResponse.success(MESSAGE);
    }

    @PatchMapping
    public ApiResponse<String> pingPatch() {
        return ApiResponse.success(MESSAGE);
    }

    @PutMapping
    public ApiResponse<String> pingPut() {
        return ApiResponse.success(MESSAGE);
    }
}
