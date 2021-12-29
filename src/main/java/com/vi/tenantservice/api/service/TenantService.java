package com.vi.tenantservice.api.service;

import java.time.LocalDateTime;
import java.util.Optional;

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
        tenantEntity.setCreateDate(LocalDateTime.now());
        tenantEntity.setUpdateDate(LocalDateTime.now());
        return tenantRepository.save(tenantEntity);
    }

    public TenantEntity update(TenantEntity tenantEntity) {
        tenantEntity.setUpdateDate(LocalDateTime.now());
        return tenantRepository.save(tenantEntity);
    }

    public Optional<TenantEntity> findTenantById(Long id) {
        return tenantRepository.findById(id);
    }
}
