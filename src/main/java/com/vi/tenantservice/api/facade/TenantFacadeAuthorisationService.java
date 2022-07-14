package com.vi.tenantservice.api.facade;

import com.vi.tenantservice.api.authorisation.UserRole;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantSetting;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantFacadeAuthorisationService {

  private final @NonNull AuthorisationService authorisationService;
  private final @NonNull TenantFacadeChangeDetectionService tenantFacadeChangeDetectionService;

  private boolean isSingleTenantAdmin() {
    return authorisationService.hasAuthority(UserRole.SINGLE_TENANT_ADMIN.getValue());
  }

  private boolean userHasAnyRoleOf(List<UserRole> roles) {
    return roles.stream().anyMatch(r -> authorisationService.hasAuthority(r.getValue()));
  }

  private boolean tenantNotMatching(Long id, Optional<Long> tenantId) {
    return tenantId.isEmpty() || !tenantId.get().equals(id);
  }

  void assertUserIsAuthorizedToAccessTenant(Long tenantId) {
    log.info("Asserting user is authorized to update tenant with id " + tenantId);
    if (isSingleTenantAdmin()) {
      log.info("User is single tenant admin. Checking if he has authority to modify tenant with id "
          + tenantId);
      var tenantIdFromAccessToken = authorisationService.findTenantIdInAccessToken();
      if (tenantNotMatching(tenantId, tenantIdFromAccessToken)) {
        throw new AccessDeniedException("User " + authorisationService.getUsername()
            + " not authorized to edit tenant with id: " + tenantId);
      }
    }
  }

  void assertUserHasSufficientPermissionsToChangeAttributes(
      TenantDTO sanitizedTenantDTO, TenantEntity existingTenant) {
    if (isSingleTenantAdmin()) {
      assertSingleTenantAdminHasPermissionsToChangeAttributes(sanitizedTenantDTO, existingTenant);
    }
    List<TenantSetting> determineSettingsThatChanged = tenantFacadeChangeDetectionService.determineChangedSettings(
        sanitizedTenantDTO, existingTenant);
    log.info("Detected the following changes in setting attributes: " + determineSettingsThatChanged);
    assertUserHasPermissionsToChangeSettings(determineSettingsThatChanged);
  }


  private void assertUserHasPermissionsToChangeSettings(List<TenantSetting> changedSettings) {
    if (!changedSettings.isEmpty()) {
      changedSettings.forEach(this::assertUserHasPermissionsToChangeSetting);
    }
  }

  private void assertUserHasPermissionsToChangeSetting(TenantSetting tenantSetting) {
    if (!userHasAnyRoleOf(tenantSetting.getRolesAuthorisedToChange())) {
      throw new AccessDeniedException(
          "User does not have permissions to change setting :" + tenantSetting.name());
    }
  }

  private void assertSingleTenantAdminHasPermissionsToChangeAttributes(TenantDTO sanitizedTenantDTO,
      TenantEntity existingTenant) {

    if (!Objects.equals(sanitizedTenantDTO.getSubdomain(), existingTenant.getSubdomain())) {
      throw new AccessDeniedException("Single tenant admin cannot change subdomain");
    }
    assertSingleTenantAdminDoesNotTryToChangeLicensingInformation(sanitizedTenantDTO,
        existingTenant);
  }

  private void assertSingleTenantAdminDoesNotTryToChangeLicensingInformation(
      TenantDTO sanitizedTenantDTO, TenantEntity existingTenant) {
    if (isAttemptToDeleteExistingLicensingInformation(sanitizedTenantDTO, existingTenant)) {
      throw new AccessDeniedException("Single tenant admin cannot delete licensing");
    }

    if (sanitizedTenantDTO.getLicensing() != null && licensingChanged(sanitizedTenantDTO, existingTenant)) {
        throw new AccessDeniedException(
            "Single tenant admin cannot change allowed number of users");
    }
  }

  private boolean licensingChanged(TenantDTO sanitizedTenantDTO, TenantEntity existingTenant) {
    return !Objects.equals(sanitizedTenantDTO.getLicensing().getAllowedNumberOfUsers(),
        existingTenant.getLicensingAllowedNumberOfUsers());
  }

  private boolean isAttemptToDeleteExistingLicensingInformation(TenantDTO sanitizedTenantDTO,
      TenantEntity existingTenant) {
    return sanitizedTenantDTO.getLicensing() == null
        && existingTenant.getLicensingAllowedNumberOfUsers() != null;
  }
}
