package com.qaisarabbas.coregateway.services;

import com.qaisarabbas.coregateway.exception.GlobalException;
import com.qaisarabbas.coregateway.model.ApiResponse;
import com.qaisarabbas.coregateway.model.LoginRequest;
import com.qaisarabbas.coregateway.persistance.entities.VendorToken;
import com.qaisarabbas.coregateway.persistance.entities.Vendors;
import com.qaisarabbas.coregateway.persistance.repository.VendorTokenRepository;
import com.qaisarabbas.coregateway.persistance.repository.VendorsRepository;
import com.qaisarabbas.coregateway.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtService;
    private final VendorsRepository vendorsRepository;
    private final VendorTokenRepository vendorTokenRepository;

    public ApiResponse<Map<String, Object>> generateToken(LoginRequest request) {
        Vendors vendor = vendorsRepository.findByClientIdAndIsActive(request.getClient_id(), true)
                .orElseThrow(() -> new GlobalException("This client id " + request.getClient_id() +" not found!"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getClient_id(),request.getClient_secret())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(vendor);
        if (token == null) {
            log.error("Failed to generate token of client id: {}", request.getClient_id());
            throw new GlobalException("Failed to generate token");
        }

        VendorToken vendorToken = new VendorToken();
        vendorToken.setVendorId(vendor.getId());
        vendorToken.setToken(token);
        vendorToken.setCreatedAt(LocalDateTime.now());
        vendorToken.setIpAddress("IP Address");
        vendorTokenRepository.save(vendorToken);

        Map<String, Object> response = new HashMap<>();
        response.put("client_id", vendor.getClientId());
        response.put("client_name", vendor.getClientName());
        response.put("token", token);

        return ApiResponse.success(response);
    }
}
