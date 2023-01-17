package com.vi.tenantservice.api.repository;

import com.vi.tenantservice.api.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {

  TenantEntity findBySubdomain(String subdomain);
}
