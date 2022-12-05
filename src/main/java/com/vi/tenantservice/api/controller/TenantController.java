package com.vi.tenantservice.api.controller;

import com.vi.tenantservice.api.facade.TenantServiceFacade;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantMultilingualDTO;
import com.vi.tenantservice.config.security.AuthorisationService;
import com.vi.tenantservice.generated.api.controller.TenantApi;
import com.vi.tenantservice.generated.api.controller.TenantadminApi;
import io.swagger.annotations.Api;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller for tenant API operations.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "tenant-controller")
@Slf4j
public class TenantController implements TenantApi, TenantadminApi {

  private final @NonNull TenantServiceFacade tenantServiceFacade;
  private final @NonNull AuthorisationService authorisationService;

  @Override
  @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
  public ResponseEntity<TenantDTO> getTenantById(Long id,  @CookieValue(name = "lang", required = false, defaultValue = "de") String lang) {

    var tenantById = tenantServiceFacade.findTenantById(id);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAuthority('tenant-admin')")
  public ResponseEntity<List<BasicTenantLicensingDTO>> getAllTenants(@CookieValue(name = "lang", required = false, defaultValue = "de") String lang) {
    var tenants = tenantServiceFacade.getAllTenants();
    return !CollectionUtils.isEmpty(tenants)
        ? new ResponseEntity<>(tenants, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @PreAuthorize("hasAuthority('tenant-admin')")
  public ResponseEntity<TenantMultilingualDTO> createTenant(@Valid TenantMultilingualDTO tenantMultilingualDTO) {
    log.info("Creating tenant with by user {} ", authorisationService.getUsername());
    var tenant = tenantServiceFacade.createTenant(tenantMultilingualDTO);
    return new ResponseEntity<>(tenant, HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
  public ResponseEntity<TenantMultilingualDTO> updateTenant(Long id, @Valid TenantMultilingualDTO tenantDTO) {
    log.info("Updating tenant with id {} by user {} ", id, authorisationService.getUsername());
    var updatedTenantDTO = tenantServiceFacade.updateTenant(id, tenantDTO);
    return new ResponseEntity<>(updatedTenantDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataBySubdomain(String subdomain, Long tenantId, @CookieValue(name = "lang", required = false, defaultValue = "de") String lang) {
    var tenantById = tenantServiceFacade.findTenantBySubdomain(subdomain, tenantId);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataByTenantId(Long tenantId, @CookieValue(name = "lang", required = false, defaultValue = "de") String lang) {
    var tenantById = tenantServiceFacade.findRestrictedTenantById(tenantId);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<RestrictedTenantDTO> getRestrictedSingleTenantData() {
    var singleTenant = tenantServiceFacade.getSingleTenant();
    return singleTenant.isEmpty() ? new ResponseEntity<>(HttpStatus.BAD_REQUEST)
        : new ResponseEntity<>(singleTenant.get(), HttpStatus.OK);

  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }
}
