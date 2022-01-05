package com.vi.tenantservice.api.controller;

import javax.validation.Valid;

import java.util.Optional;

import com.vi.tenantservice.api.facade.TenantServiceFacade;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.generated.api.controller.TenantApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for tenant API operations.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "tenant-controller")
public class TenantController implements TenantApi {

  private final @NonNull TenantServiceFacade tenantServiceFacade;

  @Override
  public ResponseEntity<TenantDTO> getTenantById(@ApiParam(value = "Tenant ID",required=true) @PathVariable("id") Long id) {
    Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(id);
    return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAuthority('technical')")
  public ResponseEntity<TenantDTO> createTenant(@ApiParam(value = "")  @Valid @RequestBody(required = false) TenantDTO tenantDTO) {
    TenantDTO tenant = tenantServiceFacade.createTenant(tenantDTO);
    return new ResponseEntity<>(tenant, HttpStatus.OK);
  }

  @Override
  @PreAuthorize("hasAuthority('technical')")
  public ResponseEntity<Void> updateTenant(@ApiParam(value = "Tenant ID",required=true) @PathVariable("id") Long id,@ApiParam(value = ""  )  @Valid @RequestBody(required = false) TenantDTO tenantDTO) {
    tenantServiceFacade.updateTenant(id, tenantDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
