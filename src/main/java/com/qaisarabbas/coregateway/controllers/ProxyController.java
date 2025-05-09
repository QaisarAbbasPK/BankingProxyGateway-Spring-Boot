package com.qaisarabbas.coregateway.controllers;

import com.qaisarabbas.coregateway.exception.GlobalException;
import com.qaisarabbas.coregateway.model.ApiResponse;
import com.qaisarabbas.coregateway.persistance.entities.ServiceRoute;
import com.qaisarabbas.coregateway.persistance.entities.Vendors;
import com.qaisarabbas.coregateway.persistance.repository.ServiceRouteRepository;
import com.qaisarabbas.coregateway.persistance.repository.VendorsRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/gateway/v1/")
@CrossOrigin
public class ProxyController {

    private final ConcurrentHashMap<String, RestTemplate> restTemplateCache = new ConcurrentHashMap<>();
    private final ServiceRouteRepository serviceRouteRepository;
    private final VendorsRepository vendorsRepository;

    public ProxyController(ServiceRouteRepository serviceRouteRepository,
                           VendorsRepository vendorsRepository) {
        this.serviceRouteRepository = serviceRouteRepository;
        this.vendorsRepository = vendorsRepository;
    }

    @RequestMapping("/{service}/**")
    public ResponseEntity<?> proxyRequest(
            @PathVariable String service,
            HttpMethod method,
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        Vendors vendor = vendorsRepository.findByClientIdAndIsActive(userDetails.getUsername(), true).orElseThrow(
                () -> new GlobalException("This client id " + userDetails.getUsername() +" not found!")
        );

        ServiceRoute route = serviceRouteRepository.findByServiceKeyAndVendorIdAndIsActive(service.trim(), vendor.getId(), true).orElseThrow(
                () -> new GlobalException("Service not found: " + service)
        );


        int connectTimeout = Optional.ofNullable(route.getConnectTimeout()).orElse(30_000);
        int readTimeout = Optional.ofNullable(route.getReadTimeout()).orElse(30_000);

        String cacheKey = connectTimeout + ":" + readTimeout;

        RestTemplate dynamicRestTemplate = restTemplateCache.computeIfAbsent(cacheKey, key -> {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(connectTimeout);
            requestFactory.setReadTimeout(readTimeout);
            return new RestTemplate(requestFactory);
        });

        String backendBaseUrl = route.getServiceUrl();

        String forwardPath = extractForwardPath(request, service);
        String queryString = request.getQueryString();
        String targetUrl = backendBaseUrl + forwardPath + (queryString != null ? "?" + queryString : "");

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                headers.add(headerName, request.getHeader(headerName))
        );

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {

            ResponseEntity<byte[]> response = dynamicRestTemplate.exchange(targetUrl, method, entity, byte[].class);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Catch 4xx and 5xx responses and pass them back
            ex.getResponseHeaders();
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(ex.getResponseHeaders())
                    .body(ex.getResponseBodyAsByteArray());

        } catch (ResourceAccessException ex) {
            // Connection/timeout errors
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.error(400, "Gateway timeout or resource access error: " + ex.getMessage()));

        } catch (Exception ex) {
            // Other unexpected exceptions
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.error(400, "Proxy error: " + ex.getMessage()));

        }
    }

    private String extractForwardPath(HttpServletRequest request, String service) {
        String fullPath = request.getRequestURI();
        String basePath = "/gateway/v1/" + service;
        return fullPath.substring(basePath.length());
    }
}
