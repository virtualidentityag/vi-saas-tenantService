package com.vi.tenantservice.api.controller;

import com.vi.tenantservice.api.facade.TenantServiceFacade;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.config.security.AuthorisationService;
import com.vi.tenantservice.generated.api.controller.PublicApi;
import com.vi.tenantservice.generated.api.controller.TenantApi;
import io.swagger.annotations.Api;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Controller for tenant API operations.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "tenant-controller")
@Slf4j
public class TenantController implements TenantApi, PublicApi {

  private final @NonNull TenantServiceFacade tenantServiceFacade;
  private final @NonNull AuthorisationService authorisationService;

  @Override
  @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
  public ResponseEntity<TenantDTO> getTenantById(Long id) {

    var tenantById = tenantServiceFacade.findTenantById(id);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAuthority('tenant-admin')")
  public ResponseEntity<TenantDTO> createTenant(TenantDTO tenantDTO) {
    log.info("Creating tenant with by user {} ", authorisationService.getUsername());
    var tenant = tenantServiceFacade.createTenant(tenantDTO);
    return new ResponseEntity<>(tenant, HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
  public ResponseEntity<TenantDTO> updateTenant(Long id, TenantDTO tenantDTO) {
    log.info("Updating tenant with id {} by user {} ", id, authorisationService.getUsername());
    var updatedTenantDTO = tenantServiceFacade.updateTenant(id, tenantDTO);
    return new ResponseEntity<>(updatedTenantDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataBySubdomain(String subdomain) {
    var tenantById = tenantServiceFacade.findTenantBySubdomain(subdomain);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return TenantApi.super.getRequest();
  }
}
