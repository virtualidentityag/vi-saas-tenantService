package com.vi.tenantservice.api.controller;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vi.tenantservice.TenantServiceApplication;
import com.vi.tenantservice.api.util.TenantTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest(classes = TenantServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureMockMvc(addFilters = false)
public class TenantControllerIT {

    public static final String TENANT_CREATE = "/tenant";
    public static final String EXISTING_TENANT = "/tenant/1";
    public static final String NON_EXISTING_TENANT = "/tenant/2";
    public static final String AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS = "tenant-admin";
    public static final String AUTHORITY_WITHOUT_TENANT_MODIFY_PERMISSIONS = "technical";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    TenantTestDataBuilder tenantTestDataBuilder = new TenantTestDataBuilder();

    @Test
    public void createTenant_Should_returnStatusOk_When_calledWithValidTenantCreateParamsAndValidAuthority()
            throws Exception {

        mockMvc.perform(post(TENANT_CREATE)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createTenant_Should_returnStatusForbidden_When_calledWithInvalidUserAuthoririty()
            throws Exception {

        mockMvc.perform(post(TENANT_CREATE)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITHOUT_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createTenant_Should_notCreateTenant_When_SubdomainIsNotUnique()
            throws Exception {
        // given
        mockMvc.perform(post(TENANT_CREATE)
                        .contentType(APPLICATION_JSON)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS))
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("sub").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        // when
        mockMvc.perform(post(TENANT_CREATE)
                        .contentType(APPLICATION_JSON)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS))
                        .content(tenantTestDataBuilder.withName("another tenant").withSubdomain("sub").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updateTenant_Should_returnStatusOk_When_calledWithValidTenantCreateParamsAndValidAuthority()
            throws Exception {
        mockMvc.perform(put(EXISTING_TENANT)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("changed subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void updateTenant_Should_returnStatusForbidden_When_calledWithValidTenantCreateParamsAndInvalidAuthority()
            throws Exception {
        mockMvc.perform(put(EXISTING_TENANT)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITHOUT_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("changed subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateTenant_Should_returnStatusNotFound_When_UpdateAttemptForNonExistingTenant()
            throws Exception {
        mockMvc.perform(put(NON_EXISTING_TENANT)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITH_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("changed subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTenant_Should_returnStatusOk_When_calledWithExistingTenantIdAndForAnyValidAuthority()
            throws Exception {
        mockMvc.perform(get(EXISTING_TENANT)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITHOUT_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.subdomain").exists())
                .andExpect(jsonPath("$.licensing").exists())
                .andExpect(jsonPath("$.licensing").exists())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getTenant_Should_returnStatusNotFound_When_calledWithNotExistingTenantId()
            throws Exception {
        mockMvc.perform(get(NON_EXISTING_TENANT)
                        .with(user("not important").authorities((GrantedAuthority) () -> AUTHORITY_WITHOUT_TENANT_MODIFY_PERMISSIONS))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTenant_Should_returnStatusForbidden_When_calledWithoutAnyAuthorization()
            throws Exception {
        mockMvc.perform(get(EXISTING_TENANT)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
