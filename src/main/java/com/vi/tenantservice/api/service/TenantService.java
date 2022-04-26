package com.vi.tenantservice.api.service;

import static com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason.SUBDOMAIN_NOT_UNIQUE;

import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.repository.TenantRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
        setCreateAndUpdateDate(tenantEntity);
        return tenantRepository.save(tenantEntity);
    }

    private void setCreateAndUpdateDate(TenantEntity tenantEntity) {
        tenantEntity.setCreateDate(LocalDateTime.now(ZoneOffset.UTC));
        tenantEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
    }

    private void validateTenantSubdomainDoesNotExist(TenantEntity tenantEntity) {
       var dbTenant = tenantRepository.findBySubdomain(tenantEntity.getSubdomain());
        if (tenantWithSuchSubdomainAlreadyExists(tenantEntity, dbTenant)) {
            throw new TenantValidationException(SUBDOMAIN_NOT_UNIQUE);
        }
    }

    private boolean tenantWithSuchSubdomainAlreadyExists(TenantEntity tenantEntity, TenantEntity dbTenant) {
        return dbTenant != null && !dbTenant.equals(tenantEntity);
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

    public List<TenantEntity> getAllTenants() {
        return tenantRepository.findAll();
    }
}
