package com.vi.tenantservice.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TenantServiceFacadeTest {

  private static final long ID = 1L;
  private static final String MULTI_TENANT_ADMIN = "tenant-admin";
  private static final String TENANT_ID = "tenantId";
  private final TenantDTO tenantDTO = new TenantDTO();
  private final TenantEntity tenantEntity = new TenantEntity();

  @Mock
  private TenantConverter converter;

  @Mock
  private TenantService tenantService;

  @Mock
  private AuthorisationService authorisationService;

  @InjectMocks
  private TenantServiceFacade tenantServiceFacade;

  @Test
  void createTenant_Should_createTenant() {
    // given
    when(converter.toEntity(tenantDTO)).thenReturn(tenantEntity);

    // when
    tenantServiceFacade.createTenant(tenantDTO);

    // then
    verify(converter).toEntity(tenantDTO);
    verify(tenantService).create(tenantEntity);
  }

  @Test
  void updateTenant_Should_updateTenant_When_tenantIsFoundAndUserIsMultipleTenantAdmin() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toEntity(tenantEntity, tenantDTO)).thenReturn(tenantEntity);
    when(authorisationService.hasAuthority(MULTI_TENANT_ADMIN)).thenReturn(true);

    // when
    tenantServiceFacade.updateTenant(ID, tenantDTO);

    // then
    verify(tenantService).findTenantById(ID);
    verify(converter).toEntity(tenantEntity, tenantDTO);
    verify(tenantService).update(tenantEntity);
  }

  @Test
  void updateTenant_Should_ThrowTenantNotFoundException_When_IdNotFound() {
    // given
    when(authorisationService.hasAuthority(MULTI_TENANT_ADMIN)).thenReturn(true);

    // then
    assertThrows(TenantNotFoundException.class, () -> {

      // when
      tenantServiceFacade.updateTenant(ID, tenantDTO);
    });
    verify(tenantService).findTenantById(ID);
  }

  @Test
  void updateTenant_Should_updateTenant_When_tenantIsFoundAndUserIsSingleTenantAdminForThatTenant() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toEntity(tenantEntity, tenantDTO)).thenReturn(tenantEntity);
    when(authorisationService.hasAuthority(MULTI_TENANT_ADMIN)).thenReturn(false);
    when(authorisationService.findCustomUserAttributeInAccessToken(TENANT_ID))
        .thenReturn(Optional.of(ID));

    // when
    tenantServiceFacade.updateTenant(ID, tenantDTO);

    // then
    verify(tenantService).findTenantById(ID);
    verify(converter).toEntity(tenantEntity, tenantDTO);
    verify(tenantService).update(tenantEntity);
  }

  @Test
  void updateTenant_Should_ThrowAccessDeniedException_When_UserIsSingleTenantAdminAndDoesNotHaveAnyTokenIdKeycloakAttribute() {
    // given
    when(authorisationService.hasAuthority(MULTI_TENANT_ADMIN)).thenReturn(false);
    when(authorisationService.findCustomUserAttributeInAccessToken(TENANT_ID))
        .thenReturn(Optional.empty());
    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantServiceFacade.updateTenant(ID, tenantDTO);
    });
    verify(tenantService, Mockito.never()).findTenantById(ID);
  }

  @Test
  void updateTenant_Should_ThrowAccessDeniedException_When_UserIsSingleTenantAdminAndDoesAndTokenIdAttributeDoesNotMatch() {
    // given
    when(authorisationService.hasAuthority(MULTI_TENANT_ADMIN)).thenReturn(false);
    Long notMatchingId = ID + 1;
    when(authorisationService.findCustomUserAttributeInAccessToken(TENANT_ID))
        .thenReturn(Optional.of(notMatchingId));
    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantServiceFacade.updateTenant(ID, tenantDTO);
    });
    verify(tenantService, Mockito.never()).findTenantById(ID);
  }

  @Test
  void findTenantById_Should_notFindTenant_When_NotExistingIdIsPassedForSingleTenantAdmin() {
    // given
    when(authorisationService.findCustomUserAttributeInAccessToken(TENANT_ID))
        .thenReturn(Optional.of(2L));

    // when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(2L);

    // then
    assertThat(tenantById).isNotPresent();
  }

  @Test
  void findTenantById_Should_findTenant_When_ExistingIdIsPassedForSingleTenantAdmin() {
    // given
    when(authorisationService.findCustomUserAttributeInAccessToken(TENANT_ID))
        .thenReturn(Optional.of(ID));
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toDTO(tenantEntity)).thenReturn(tenantDTO);
    // when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(ID);
    assertThat(tenantById).contains(tenantDTO);
  }
}
