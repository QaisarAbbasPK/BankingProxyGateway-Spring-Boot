package com.qaisarabbas.coregateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private T response;

    public ApiResponse(int code, String message, T response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(200, "Request successful", response);
    }

    public static <T> ApiResponse<T> success(String message, T response) {
        return new ApiResponse<>(200, message, response);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

