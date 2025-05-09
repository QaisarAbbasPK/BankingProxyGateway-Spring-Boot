package com.qaisarabbas.coregateway.persistance.repository;

import com.qaisarabbas.coregateway.persistance.entities.VendorToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorTokenRepository extends JpaRepository<VendorToken, Long> {
}
