package com.vi.tenantservice.api.repository;

import com.vi.tenantservice.api.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {

    @Query("select t from TenantEntity t where t.subdomain = ?1")
    TenantEntity findBySubdomain(String subdomain);
}
