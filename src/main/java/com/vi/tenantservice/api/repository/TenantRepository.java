package com.vi.tenantservice.api.repository;

import com.vi.tenantservice.api.model.TenantEntity;
import org.springframework.data.repository.CrudRepository;

public interface TenantRepository extends CrudRepository<TenantEntity, Long> {


}
