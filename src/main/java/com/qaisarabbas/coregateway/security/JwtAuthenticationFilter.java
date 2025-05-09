package com.qaisarabbas.coregateway.security;

import com.qaisarabbas.coregateway.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String token = extractToken(request);
        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Wrap the request to exclude the Auth-Access-Token header
            HttpServletRequest wrappedRequest = new HeaderRemovingRequestWrapper(request, "Auth-Access-Token");
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Auth-Access-Token");
        if (header != null && header.startsWith("Secret ")) {
            return header.substring(7);
        }
        return null;
    }

    private static class HeaderRemovingRequestWrapper extends HttpServletRequestWrapper {
        private final String headerToRemove;

        public HeaderRemovingRequestWrapper(HttpServletRequest request, String headerToRemove) {
            super(request);
            this.headerToRemove = headerToRemove.toLowerCase();
        }

        @Override
        public String getHeader(String name) {
            if (name.equalsIgnoreCase(headerToRemove)) {
                return null;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> filtered = Collections.list(super.getHeaderNames())
                    .stream()
                    .filter(name -> !name.equalsIgnoreCase(headerToRemove))
                    .collect(Collectors.toList());
            return Collections.enumeration(filtered);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (name.equalsIgnoreCase(headerToRemove)) {
                return Collections.emptyEnumeration();
            }
            return super.getHeaders(name);
        }
    }
}
