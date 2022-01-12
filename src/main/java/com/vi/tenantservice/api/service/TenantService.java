package com.vi.tenantservice.api.service;

import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.repository.TenantRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {

    private final @NonNull TenantRepository tenantRepository;

    public TenantEntity create(TenantEntity tenantEntity) {
        validateTenantSubdomainDoesNotExist(tenantEntity);
        tenantEntity.setCreateDate(LocalDateTime.now(ZoneOffset.UTC));
        tenantEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
        return tenantRepository.save(tenantEntity);
    }

    private void validateTenantSubdomainDoesNotExist(TenantEntity tenantEntity) {
       var dbTenant = tenantRepository.findBySubdomain(tenantEntity.getSubdomain());
        if (dbTenant != null  && !dbTenant.equals(tenantEntity)) {
            throw new TenantValidationException("Tenant with this subdomain already exists");
        }
    }

    public TenantEntity update(TenantEntity tenantEntity) {
        validateTenantSubdomainDoesNotExist(tenantEntity);
        tenantEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
        return tenantRepository.save(tenantEntity);
    }

    public Optional<TenantEntity> findTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public Optional<TenantEntity> findTenantBySubdomain(String subdomain) {
        var bySubdomain = tenantRepository.findBySubdomain(subdomain);
        return bySubdomain != null ? Optional.of(bySubdomain) : Optional.empty();
    }
}
