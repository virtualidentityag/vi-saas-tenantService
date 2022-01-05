package com.vi.tenantservice.api.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TenantService {

    @Autowired
    TenantRepository tenantRepository;

    public TenantEntity create(TenantEntity tenantEntity) {
        validate(tenantEntity);
        tenantEntity.setCreateDate(LocalDateTime.now());
        tenantEntity.setUpdateDate(LocalDateTime.now());
        return tenantRepository.save(tenantEntity);
    }

    private void validate(TenantEntity tenantEntity) {
        validateTenantSubdomainDoesNotExist(tenantEntity);
    }

    private void validateTenantSubdomainDoesNotExist(TenantEntity tenantEntity) {
       TenantEntity dbTenant = tenantRepository.findBySubdomain(tenantEntity.getSubdomain());
        if (dbTenant != null  && !dbTenant.equals(tenantEntity)) {
            throw new TenantValidationException("Tenant with this subdomain already exists");
        }
    }

   /* private Predicate<TenantEntity> tenantIdNotEqualPredicate(TenantEntity tenantEntity) {
        return dbTenant -> idNotEqual(tenantEntity, dbTenant);
    }*/

  /*  private boolean idNotEqual(TenantEntity tenantEntity, TenantEntity dbTenant) {
        return !dbTenant.getId().equals(tenantEntity.getId());
    }*/

    public TenantEntity update(TenantEntity tenantEntity) {
        validate(tenantEntity);
        tenantEntity.setUpdateDate(LocalDateTime.now());
        return tenantRepository.save(tenantEntity);
    }

    public Optional<TenantEntity> findTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public Optional<TenantEntity> findTenantBySubdomain(String subdomain) {
        TenantEntity bySubdomain = tenantRepository.findBySubdomain(subdomain);
        return bySubdomain != null ? Optional.of(bySubdomain) : Optional.empty();
    }
}
