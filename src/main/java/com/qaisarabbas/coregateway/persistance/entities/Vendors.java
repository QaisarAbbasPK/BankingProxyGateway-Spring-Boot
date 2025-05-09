package com.qaisarabbas.coregateway.persistance.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "vendors")
public class Vendors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "client_name")
    private String clientName;

    @Column(nullable = false, name = "client_id", unique = true)
    private String clientId;

    @Column(nullable = false, name = "client_secret", unique = true)
    private String clientSecret;

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
