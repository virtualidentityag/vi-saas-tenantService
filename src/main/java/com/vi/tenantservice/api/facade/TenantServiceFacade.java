package com.vi.tenantservice.api.facade;


import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Facade to encapsulate services and logic needed to manage tenants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceFacade {

  private final @NonNull TenantService tenantService;
  private final @NonNull TenantConverter tenantConverter;
  private final @NonNull TenantInputSanitizer tenantInputSanitizer;
  private final @NonNull TenantFacadeAuthorisationService tenantFacadeAuthorisationService;

  public TenantDTO createTenant(TenantDTO tenantDTO) {
    log.info("Creating new tenant");
    TenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    var entity = tenantConverter.toEntity(sanitizedTenantDTO);
    return tenantConverter.toDTO(tenantService.create(entity));
  }

  public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
    tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
    TenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    log.info("Attempting to update tenant with id {}", id);
    return updateWithSanitizedInput(id, sanitizedTenantDTO);
  }

  private TenantDTO updateWithSanitizedInput(Long id, TenantDTO sanitizedTenantDTO) {
    var tenantById = tenantService.findTenantById(id);
    if (tenantById.isPresent()) {
      return updateExistingTenant(sanitizedTenantDTO, tenantById.get());
    } else {
      throw new TenantNotFoundException("Tenant with given id could not be found : " + id);
    }
  }

  private TenantDTO updateExistingTenant(TenantDTO sanitizedTenantDTO,
      TenantEntity existingTenant) {
    tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(sanitizedTenantDTO, existingTenant);
    var updatedEntity = tenantConverter.toEntity(existingTenant, sanitizedTenantDTO);
    log.info("Tenant with id {} updated", existingTenant.getId());
    updatedEntity = tenantService.update(updatedEntity);
    return tenantConverter.toDTO(updatedEntity);
  }

  public Optional<TenantDTO> findTenantById(Long id) {
    tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
    var tenantById = tenantService.findTenantById(id);
    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toDTO(tenantById.get()));
  }

  public Optional<RestrictedTenantDTO> findRestrictedTenantById(Long id) {
    var tenantById = tenantService.findTenantById(id);
    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantById.get()));
  }
  
  public List<BasicTenantLicensingDTO> getAllTenants() {
    var tenantEntities = tenantService.getAllTenants();
    return tenantEntities.stream().map(tenantConverter::toBasicLicensingTenantDTO).collect(
        Collectors.toList());
  }

  public Optional<RestrictedTenantDTO> findTenantBySubdomain(String subdomain) {
    var tenantById = tenantService.findTenantBySubdomain(subdomain);
    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantById.get()));
  }
}
