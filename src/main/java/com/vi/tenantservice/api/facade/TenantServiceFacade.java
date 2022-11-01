package com.vi.tenantservice.api.facade;


import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
  private final @NonNull AuthorisationService authorisationService;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomain;

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

  public Optional<RestrictedTenantDTO> findTenantBySubdomain(String subdomain, Long tenantId) {
    var tenantById = tenantService.findTenantBySubdomain(subdomain);

    Optional<Long> tenant = authorisationService.resolveTenantFromRequest(tenantId);
    if (multitenancyWithSingleDomain && tenant.isPresent()) {
      return getSingleDomainSpecificTenantData(tenantById, tenantId);
    }

    return tenantById.isEmpty() ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantById.get()));
  }

  public Optional<RestrictedTenantDTO> getSingleDomainSpecificTenantData(
      Optional<TenantEntity> tenant, Long tenantId) {

    if (tenantId == null) {
      Optional<Long> accessTokenTenantId = authorisationService.findTenantIdInAccessToken();
      if (accessTokenTenantId.isEmpty()) {
        if (accessTokenTenantId.isEmpty()) {
          throw new BadRequestException("tenantId not found in access token");
        }
      }
      tenantId = accessTokenTenantId.get();
    }

    Optional<TenantEntity> tenantFromAuthorisedContext = tenantService.findTenantById(tenantId);
    if (tenantFromAuthorisedContext.isEmpty()) {
      throw new BadRequestException("Tenant not found for id " + tenantId);
    }
    return Optional.of(tenantConverter
        .toRestrictedTenantDTOinAuthorisedContext(tenant.get(),
            tenantFromAuthorisedContext.get()));
  }

  public Optional<RestrictedTenantDTO> getSingleTenant() {
    var tenantEntities = tenantService.getAllTenants();
    if (tenantEntities != null && tenantEntities.size() == 1) {
      var tenantEntity = tenantEntities.get(0);
      return Optional.of(tenantConverter.toRestrictedTenantDTO(tenantEntity));
    } else {
      throw new IllegalStateException("Not exactly one tenant was found.");
    }
  }
}
