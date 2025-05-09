package com.qaisarabbas.coregateway.persistance.repository;

import com.qaisarabbas.coregateway.persistance.entities.ServiceRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRouteRepository extends JpaRepository<ServiceRoute, Long> {
    Optional<ServiceRoute> findByServiceKey(String serviceKey);
    Optional<ServiceRoute> findByServiceKeyAndVendorIdAndIsActive(String serviceKey, Long vendorId, Boolean isActive);
}
