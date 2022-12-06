package com.vi.tenantservice.api.facade;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantMultilingualDTO;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.service.TranslationService;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import com.vi.tenantservice.config.security.AuthorisationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceFacadeTest {

  private static final long ID = 1L;
  public static final String DE = "de";
  private final TenantMultilingualDTO tenantMultilingualDTO = new TenantMultilingualDTO();
  private final TenantDTO tenantDTO = new TenantDTO();
  private final TenantMultilingualDTO sanitizedTenantDTO = new TenantMultilingualDTO();
  private final RestrictedTenantDTO restrictedTenantDTO = new RestrictedTenantDTO();
  private final TenantEntity tenantEntity = new TenantEntity();

  @Mock
  private TenantConverter converter;

  @Mock
  private TenantService tenantService;

  @Mock
  private TenantInputSanitizer tenantInputSanitizer;

  @Mock
  private TenantFacadeAuthorisationService tenantFacadeAuthorisationService;

  @Mock
  private AuthorisationService authorisationService;

  @Mock
  private TranslationService translationService;

  @InjectMocks
  private TenantServiceFacade tenantServiceFacade;

  @Test
  void createTenant_Should_createTenant() {
    // given
    when(tenantInputSanitizer.sanitize(tenantMultilingualDTO)).thenReturn(sanitizedTenantDTO);
    when(converter.toEntity(tenantMultilingualDTO)).thenReturn(tenantEntity);

    // when
    tenantServiceFacade.createTenant(tenantMultilingualDTO);

    // then
    verify(converter).toEntity(sanitizedTenantDTO);
    verify(tenantService).create(tenantEntity);
  }

  @Test
  void updateTenant_Should_updateTenant_When_tenantIsFoundAndUserIsMultipleTenantAdmin() {
    // given
    when(tenantInputSanitizer.sanitize(tenantMultilingualDTO)).thenReturn(sanitizedTenantDTO);
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toEntity(tenantEntity, sanitizedTenantDTO)).thenReturn(tenantEntity);

    // when
    tenantServiceFacade.updateTenant(ID, tenantMultilingualDTO);

    // then
    verify(tenantService).findTenantById(ID);
    verify(converter).toEntity(tenantEntity, sanitizedTenantDTO);
    verify(tenantService).update(tenantEntity);
  }

  @Test
  void updateTenant_Should_ThrowTenantNotFoundException_When_IdNotFound() {
    // then
    assertThrows(TenantNotFoundException.class, () -> {

      // when
      tenantServiceFacade.updateTenant(ID, tenantMultilingualDTO);
    });
    verify(tenantService).findTenantById(ID);
  }

  @Test
  void updateTenant_Should_updateTenant_When_tenantIsFoundAndUserIsSingleTenantAdminForThatTenant() {
    // given
    when(tenantInputSanitizer.sanitize(tenantMultilingualDTO)).thenReturn(sanitizedTenantDTO);
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(converter.toEntity(tenantEntity, sanitizedTenantDTO)).thenReturn(tenantEntity);

    // when
    tenantServiceFacade.updateTenant(ID, tenantMultilingualDTO);

    // then
    verify(tenantService).findTenantById(ID);
    verify(converter).toEntity(tenantEntity, sanitizedTenantDTO);
    verify(tenantService).update(tenantEntity);
  }

  @Test
  void updateTenant_Should_ThrowAccessDeniedException_When_UserNotAuthorizedToPerformOperation() {
    // given
    doThrow(AccessDeniedException.class).when(tenantFacadeAuthorisationService)
        .assertUserIsAuthorizedToAccessTenant(ID);
    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantServiceFacade.updateTenant(ID, tenantMultilingualDTO);
    });
    verify(tenantService, Mockito.never()).findTenantById(ID);
  }

  @Test
  void updateTenant_Should_ThrowAccessDeniedException_When_UserIsSingleTenantAdminAndDoesAndTokenIdAttributeDoesNotMatch() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(tenantInputSanitizer.sanitize(tenantMultilingualDTO)).thenReturn(sanitizedTenantDTO);

    Mockito.doThrow(AccessDeniedException.class)
        .when(tenantFacadeAuthorisationService)
        .assertUserHasSufficientPermissionsToChangeAttributes(Mockito.any(TenantMultilingualDTO.class),
            Mockito.any(TenantEntity.class));

    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantServiceFacade.updateTenant(ID, tenantMultilingualDTO);
    });
  }

  @Test
  void findTenantById_Should_notFindTenant_When_NotExistingIdIsPassedForSingleTenantAdmin() {
    // when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(2L);

    // then
    assertThat(tenantById).isNotPresent();
  }

  @Test
  void findTenantById_Should_findTenant_When_ExistingIdIsPassedForSingleTenantAdmin() {
    // given
    when(tenantService.findTenantById(ID)).thenReturn(Optional.of(tenantEntity));
    when(translationService.getCurrentLanguageContext()).thenReturn("de");
    when(converter.toDTO(tenantEntity, "de")).thenReturn(tenantDTO);
    // when
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(ID);
    assertThat(tenantById).contains(tenantDTO);
  }

  @Test
  void getAllTenant_Should_CallServiceToGetAllTenants() {
    // when
    tenantServiceFacade.getAllTenants();
    // then
    verify(tenantService).getAllTenants();
  }

  @Test
  void getSingleTenant_Should_findTenant_When_onlyOneTenantIsPresent() {
    // given
    when(tenantService.getAllTenants()).thenReturn(List.of(tenantEntity));
    when(translationService.getCurrentLanguageContext()).thenReturn(DE);
    when(converter.toRestrictedTenantDTO(tenantEntity, DE)).thenReturn(restrictedTenantDTO);

    // when
    tenantServiceFacade.getSingleTenant();

    // then
    verify(tenantService).getAllTenants();
    verify(converter).toRestrictedTenantDTO(tenantEntity, DE);
  }

  @Test
  void getSingleTenant_Should_shouldThrowIllegalStateException_When_moreTenantsArePresent() {
    // given
    TenantEntity secondTenantEntity = new TenantEntity();
    secondTenantEntity.setId(2L);
    when(tenantService.getAllTenants()).thenReturn(List.of(tenantEntity, secondTenantEntity));

    // then
    assertThrows(IllegalStateException.class, () -> {
      // when
      tenantServiceFacade.getSingleTenant();
    });

    verify(tenantService).getAllTenants();
    verifyNoInteractions(converter);
  }

  @Test
  void findTenantBySubdomain_Should_returnTenantAwareData_When_RequestIsTenantAware(){
    String subdomain = "app";
    ReflectionTestUtils.setField(tenantServiceFacade,"multitenancyWithSingleDomain",true);
    ReflectionTestUtils.setField(tenantServiceFacade,"tenantConverter",new TenantConverter());

    TenantEntity defaultTenantEntity = new TenantEntity();
    defaultTenantEntity.setContentPrivacy("[{\"de\":\"content1\"}]");
    Optional<TenantEntity> defaultTenant = Optional.of(defaultTenantEntity);

    TenantEntity accessTokenTenant = new TenantEntity();
    accessTokenTenant.setContentPrivacy("[{\"de\":\"content2\"}]");
    Optional<TenantEntity> accessTokenTenantData = Optional.of(accessTokenTenant);

    when(tenantService.findTenantBySubdomain(subdomain)).thenReturn(defaultTenant);
    when(authorisationService.resolveTenantFromRequest(null)).thenReturn(Optional.of(2L));
    when(tenantService.findTenantById(2L)).thenReturn(accessTokenTenantData);

    Optional<RestrictedTenantDTO> tenantDTO = tenantServiceFacade.findTenantBySubdomain(subdomain, null);
    assertThat(tenantDTO.get().getContent().getPrivacy()).contains("content2");

  }

}
