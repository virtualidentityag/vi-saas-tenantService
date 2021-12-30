package com.vi.tenantservice.api.repository;

import java.util.List;

import com.vi.tenantservice.api.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {

    List<TenantEntity> findBySubdomain(String subdomain);
}
