package com.qaisarabbas.coregateway.services;

import com.qaisarabbas.coregateway.persistance.entities.Vendors;
import com.qaisarabbas.coregateway.persistance.repository.VendorsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final VendorsRepository vendorsRepository;

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Vendors vendor = vendorsRepository.findByClientIdAndIsActive(clientId, true)
                .orElseThrow(() -> new UsernameNotFoundException("Vendor not found: " + clientId));
        return org.springframework.security.core.userdetails.User
                .withUsername(clientId)
                .password(vendor.getClientSecret())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
