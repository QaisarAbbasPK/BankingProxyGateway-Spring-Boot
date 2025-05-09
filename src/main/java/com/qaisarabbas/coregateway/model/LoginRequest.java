package com.qaisarabbas.coregateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String client_id;
    private String client_secret;
}
