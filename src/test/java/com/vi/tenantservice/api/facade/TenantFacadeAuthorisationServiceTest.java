package com.vi.tenantservice.api.facade;

import static com.vi.tenantservice.api.authorisation.UserRole.SINGLE_TENANT_ADMIN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vi.tenantservice.api.model.Licensing;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TenantFacadeAuthorisationServiceTest {

  private static final long ID = 1L;

  @InjectMocks
  TenantFacadeAuthorisationService tenantFacadeAuthorisationService;

  @Mock
  AuthorisationService authorisationService;

  @Test
  void assertUserIsAuthorizedToAccessTenant_Should_AllowOperation_When_tenantIsFoundAndUserIsSingleTenantAdminForThatTenant() {
    // given
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);
    when(authorisationService.findTenantIdInAccessToken())
        .thenReturn(Optional.of(ID));

    // when
    tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(ID);

    // then
    verify(authorisationService).findTenantIdInAccessToken();
  }

  @Test
  void assertUserIsAuthorizedToAccessTenant_Should_ThrowAccessDeniedException_When_UserIsSingleTenantAdminAndDoesNotHaveAnyTokenIdKeycloakAttribute() {
    // given
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);
    when(authorisationService.findTenantIdInAccessToken())
        .thenReturn(Optional.empty());
    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(ID);
    });
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_ThrowException_When_UserIsSingleTenantAdminAndTriesToChangeLicencedNumberOfUsers() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder()
        .licensingAllowedNumberOfUsers(1).build();
    TenantDTO tenantDTO = new TenantDTO().licensing(new Licensing().allowedNumberOfUsers(2));
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
    });
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_ThrowException_When_UserIsSingleTenantAdminAndTriesToChangeLicencedNumberOfUsersFromNull() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder().build();
    TenantDTO tenantDTO = new TenantDTO().licensing(new Licensing().allowedNumberOfUsers(2));
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
    });
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_ThrowException_When_UserIsSingleTenantAdminAndTriesToChangeSubdomain() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder()
        .subdomain("old subdomain").build();
    TenantDTO tenantDTO = new TenantDTO().subdomain("new subdomain");
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
    });
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_ThrowException_When_UserIsSingleTenantAdminAndTriesToChangeSubdomainFromNull() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder().build();
    TenantDTO tenantDTO = new TenantDTO().subdomain("new subdomain");
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // then
    assertThrows(AccessDeniedException.class, () -> {
      // when
      tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
    });
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_AllowOperation_When_UserIsSingleTenantAdminAndDoesNotChangeSubdomainNorLicensing() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder()
        .subdomain("old subdomain").licensingAllowedNumberOfUsers(1).build();
    TenantDTO tenantDTO = new TenantDTO().subdomain("old subdomain").licensing(new Licensing().allowedNumberOfUsers(1)).theming(new Theming().logo("logo"));
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // when
    tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
  }

  @Test
  void assertUserHasSufficientPermissionsToChangeAttributes_Should_AllowOperation_When_UserIsSingleTenantAdmin() {
    // given
    TenantEntity tenantEntity = TenantEntity.builder()
        .subdomain("old subdomain").licensingAllowedNumberOfUsers(1).build();
    TenantDTO tenantDTO = new TenantDTO().subdomain("old subdomain").licensing(new Licensing().allowedNumberOfUsers(1)).theming(new Theming().logo("logo"));
    when(authorisationService.hasAuthority(SINGLE_TENANT_ADMIN.getValue())).thenReturn(true);

    // when
    tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(tenantDTO, tenantEntity);
  }
}