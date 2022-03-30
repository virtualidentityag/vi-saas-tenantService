package com.vi.tenantservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    private final TenantEntity tenantEntity = new TenantEntity();

    @Test
    void create_Should_CreateTenantAndSetCreationDate() {
        // when
        tenantService.create(tenantEntity);
        // then
        verify(tenantRepository).save(tenantEntity);
        assertThat(tenantEntity.getCreateDate()).isNotNull();
        assertThat(tenantEntity.getUpdateDate()).isNotNull();
    }

    @Test
    void update_Should_UpdateTenantAndModifyUpdateDate() {
        LocalDateTime previousUpdateDate = tenantEntity.getUpdateDate();
        // when
        tenantService.update(tenantEntity);
        // then
        verify(tenantRepository).save(tenantEntity);
        assertThat(tenantEntity.getUpdateDate()).isNotNull();
        assertThat(tenantEntity.getUpdateDate()).isNotEqualTo(previousUpdateDate);
    }

    @Test
    void findTenantById_Should_CallFindById() {
        // given
        long tenantId = 1L;
        // when
        tenantService.findTenantById(tenantId);
        // then
        verify(tenantRepository).findById(tenantId);
    }

    @Test
    void getAllTenants_Should_CallFindAllTenants() {
        // when
        tenantService.getAllTenants();
        // then
        verify(tenantRepository).findAll();
    }

}
