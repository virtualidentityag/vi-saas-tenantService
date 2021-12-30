package com.vi.tenantservice.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantServiceFacadeTest {

  private static final long ID = 1L;
  private TenantDTO tenantDTO = new TenantDTO();
  private TenantEntity tenantEntity = new TenantEntity();

  @Mock
  private TenantConverter converter;

  @Mock
  private TenantService tenantService;

  @InjectMocks
  private TenantServiceFacade tenantServiceFacade;

  @Test
  public void shouldCreateTenant() {
    // given
    when(converter.toEntity(tenantDTO)).thenReturn(tenantEntity);

    // when
    tenantServiceFacade.createTenant(tenantDTO);

    // then
    verify(converter).toEntity(tenantDTO);
    verify(tenantService).create(tenantEntity);
  }

  @Test
  public void shouldUpdateTenantAndThrowExceptionIfIdNotFound() {
    // given
    // then
    assertThrows(TenantNotFoundException.class, () -> {
              // when
              tenantServiceFacade.updateTenant(ID, tenantDTO);
            });
    verify(tenantService).findTenantById(ID);
  }

  @Test
  public void shouldNotFindTenantById() {
    // given, when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(ID);

    // then
    assertThat(tenantById).isNotPresent();
  }

  @Test
  public void shouldFindTenantById() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toDTO(tenantEntity)).thenReturn(tenantDTO);
    // when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(ID);
    assertThat(tenantById).isPresent();
    assertThat(tenantById.get()).isEqualTo(tenantDTO);
  }

  @Test
  public void shouldUpdateTenant() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toEntity(tenantEntity, tenantDTO)).thenReturn(tenantEntity);
    // when
    tenantServiceFacade.updateTenant(ID, tenantDTO);
    // then
    verify(tenantService).findTenantById(ID);
    verify(converter).toEntity(tenantEntity, tenantDTO);
    verify(tenantService).update(tenantEntity);
  }



}
