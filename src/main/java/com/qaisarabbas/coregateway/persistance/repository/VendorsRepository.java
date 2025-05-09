package com.qaisarabbas.coregateway.persistance.repository;

import com.qaisarabbas.coregateway.persistance.entities.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorsRepository extends JpaRepository<Vendors, Long> {
    boolean existsByClientIdAndIsActive(String clientId, Boolean isActive);
    Optional<Vendors> findByClientIdAndIsActive(String clientId, Boolean isActive);
}
