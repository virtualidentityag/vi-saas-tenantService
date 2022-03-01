package com.vi.tenantservice.api.facade;

import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.config.security.AuthorisationService;
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

  private boolean isSingleTenantAdmin() {
    return authorisationService.hasAuthority("single-tenant-admin");
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

    if (!Objects.equals(sanitizedTenantDTO.getLicensing().getAllowedNumberOfUsers(), existingTenant.getLicensingAllowedNumberOfUsers())) {
      throw new AccessDeniedException("Single tenant admin cannot change subdomain");
    }
  }

  private boolean isAttemptToDeleteExistingLicensingInformation(TenantDTO sanitizedTenantDTO,
      TenantEntity existingTenant) {
    return sanitizedTenantDTO.getLicensing() == null
        && existingTenant.getLicensingAllowedNumberOfUsers() != null;
  }
}
