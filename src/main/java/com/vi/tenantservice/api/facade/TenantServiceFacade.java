package com.vi.tenantservice.api.facade;


import java.util.Optional;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Facade to encapsulate services and logic needed to manage tenants
 */
@Service
@RequiredArgsConstructor
public class TenantServiceFacade {

    @Autowired
    TenantService tenantService;

    @Autowired
    TenantConverter tenantConverter;

    public TenantDTO createTenant(TenantDTO tenantDTO) {
        TenantEntity entity = tenantConverter.toEntity(tenantDTO);
        return tenantConverter.toDTO(tenantService.create(entity));
    }

    public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
        Optional<TenantEntity> tenantById = tenantService.findTenantById(id);
        if (tenantById.isPresent()) {
            TenantEntity updatedEntity = tenantConverter.toEntity(tenantById.get(), tenantDTO);
            tenantService.update(updatedEntity);
            return tenantConverter.toDTO(updatedEntity);
        } else {
            throw new TenantNotFoundException("Tenant with given id could not be found : " + id);
        }
    }

    public Optional<TenantDTO> findTenantById(Long id) {
        Optional<TenantEntity> tenantById = tenantService.findTenantById(id);
        return tenantById.isEmpty() ? Optional.empty() : Optional.of(tenantConverter.toDTO(tenantById.get()));
    }

    public Optional<RestrictedTenantDTO> findTenantBySubdomain(String subdomain) {
        Optional<TenantEntity> tenantById = tenantService.findTenantBySubdomain(subdomain);
        return tenantById.isEmpty() ? Optional.empty() : Optional.of(tenantConverter.toRestrictedDTO(tenantById.get()));
    }
}
