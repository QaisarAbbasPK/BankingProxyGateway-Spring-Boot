package com.qaisarabbas.coregateway.persistance.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "service_routes")
public class ServiceRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", unique = true, nullable = false)
    private Long vendorId;

    @Column(name = "service_name", unique = true, nullable = false)
    private String serviceName;

    @Column(name = "service_key", unique = true, nullable = false)
    private String serviceKey;

    @Column(name = "service_url", nullable = false)
    private String serviceUrl;

    @Column(nullable = false, name = "connect_timeout")
    private Integer connectTimeout;

    @Column(nullable = false, name = "read_timeout")
    private Integer readTimeout;

    @Column(nullable = false, name = "ip_address")
    private String ipAddress;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(name = "remarks")
    private String remarks;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "updated_by")
    private String updatedBy;
}
