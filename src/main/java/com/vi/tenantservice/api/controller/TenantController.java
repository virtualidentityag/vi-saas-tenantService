package com.vi.tenantservice.api.controller;

import javax.validation.Valid;
import java.util.Optional;

import com.vi.tenantservice.api.facade.TenantServiceFacade;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.config.security.AuthorisationService;
import com.vi.tenantservice.generated.api.controller.PublicApi;
import com.vi.tenantservice.generated.api.controller.TenantApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    AuthorisationService authorisationService;

    @Override
    @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
    public ResponseEntity<TenantDTO> getTenantById(@ApiParam(value = "Tenant ID", required = true) @PathVariable("id") Long id) {
        Optional<TenantDTO> tenantById = tenantServiceFacade.findTenantById(id);
        return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('tenant-admin')")
    public ResponseEntity<TenantDTO> createTenant(@ApiParam() @Valid @RequestBody(required = false) TenantDTO tenantDTO) {
        log.info("Creating tenant with by user {} ", authorisationService.getUsername());
        TenantDTO tenant = tenantServiceFacade.createTenant(tenantDTO);
        return new ResponseEntity<>(tenant, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('tenant-admin', 'single-tenant-admin')")
    public ResponseEntity<Void> updateTenant(@ApiParam(value = "Tenant ID", required = true) @PathVariable("id") Long id, @ApiParam() @Valid @RequestBody(required = false) TenantDTO tenantDTO) {
        log.info("Updating tenant with id {} by user {} ", id, authorisationService.getUsername());
        tenantServiceFacade.updateTenant(id, tenantDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataBySubdomain(String subdomain) {
        Optional<RestrictedTenantDTO> tenantById = tenantServiceFacade.findTenantBySubdomain(subdomain);
        return tenantById.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(tenantById.get(), HttpStatus.OK);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TenantApi.super.getRequest();
    }
}
