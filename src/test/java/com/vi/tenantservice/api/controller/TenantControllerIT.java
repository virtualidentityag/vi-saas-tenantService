package com.vi.tenantservice.api.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vi.tenantservice.TenantServiceApplication;
import com.vi.tenantservice.api.util.TenantTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(classes = TenantServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureMockMvc(addFilters = false)
public class TenantControllerIT {

    public static final String TENANT_CREATE = "/tenant";

    public static final String EXISTING_TENANT = "/tenant/1";

    public static final String NON_EXISTING_TENANT = "/tenant/2";

    @Autowired
    private MockMvc mockMvc;

    TenantTestDataBuilder tenantTestDataBuilder = new TenantTestDataBuilder();

    @Test
    public void createTenant_Should_returnStatusOk_When_calledWithValidTenantCreateParams()
            throws Exception {
        mockMvc.perform(post(TENANT_CREATE)
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTenant_Should_returnStatusOk_When_calledWithValidTenantCreateParams()
            throws Exception {
        mockMvc.perform(put(EXISTING_TENANT)
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("changed subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTenant_Should_returnStatusNotFound_When_forNonExistingTenant()
            throws Exception {
        mockMvc.perform(put(NON_EXISTING_TENANT)
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("changed subdomain").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTenant_Should_returnStatusOk_When_calledWithExistingTenantId()
            throws Exception {
        mockMvc.perform(get(EXISTING_TENANT)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
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
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createTenant_Should_notCreateTenant_When_SubdomainIsNotUnique()
            throws Exception {
        // given
        mockMvc.perform(post(TENANT_CREATE)
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("tenant").withSubdomain("sub").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        // when
        mockMvc.perform(post(TENANT_CREATE)
                        .contentType(APPLICATION_JSON)
                        .content(tenantTestDataBuilder.withName("another tenant").withSubdomain("sub").withLicensing().jsonify())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}
