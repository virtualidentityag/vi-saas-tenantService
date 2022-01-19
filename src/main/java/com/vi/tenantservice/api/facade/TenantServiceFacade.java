package com.vi.tenantservice.api.facade;


import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Facade to encapsulate services and logic needed to manage tenants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceFacade {

  private final @NonNull TenantService tenantService;
  private final @NonNull AuthorisationService authorisationService;
  private final @NonNull TenantConverter tenantConverter;
  private final @NonNull TenantInputSanitizer tenantInputSanitizer;

  public TenantDTO createTenant(TenantDTO tenantDTO) {
    log.info("Creating new tenant");
    TenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    var entity = tenantConverter.toEntity(sanitizedTenantDTO);
    return tenantConverter.toDTO(tenantService.create(entity));
  }

  public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
    assertUserIsAuthorizedToAccessTenant(id);
    TenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    log.info("Attempting to update tenant with id {}", id);
    var tenantById = tenantService.findTenantById(id);
    if (tenantById.isPresent()) {
      var updatedEntity = tenantConverter.toEntity(tenantById.get(), sanitizedTenantDTO);
      log.info("Tenant with id {} updated", id);
      updatedEntity = tenantService.update(updatedEntity);
      return tenantConverter.toDTO(updatedEntity);
    } else {
      throw new TenantNotFoundException("Tenant with given id could not be found : " + id);
    }
  }

  private void assertUserIsAuthorizedToAccessTenant(Long tenantId) {
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

  private boolean tenantNotMatching(Long id, Optional<Long> tenantId) {
    return tenantId.isEmpty() || !tenantId.get().equals(id);
  }

  private boolean isSingleTenantAdmin() {
    return !isMultitenantAdmin();
  }

  private boolean isMultitenantAdmin() {
    return authorisationService.hasAuthority("tenant-admin");
  }

  public Optional<TenantDTO> findTenantById(Long id) {
    assertUserIsAuthorizedToAccessTenant(id);
    var tenantById = tenantService.findTenantById(id);
    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toDTO(tenantById.get()));
  }

  public Optional<RestrictedTenantDTO> findTenantBySubdomain(String subdomain) {
    var tenantById = tenantService.findTenantBySubdomain(subdomain);
    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedDTO(tenantById.get()));
  }
}
