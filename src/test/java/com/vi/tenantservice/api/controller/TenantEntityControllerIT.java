package com.vi.tenantservice.api.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vi.tenantservice.TenantServiceApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TenantServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class TenantEntityControllerIT {

  public static final String TENANT_CREATE = "/tenant/create";
  public static final String USER_IDS_PARAM = "userIds";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldStartApplication() {

  }

  @Test
  @Disabled
  public void createTenant_Should_returnStatusOk_When_calledWithValidDirectMessageParams()
      throws Exception {
    mockMvc.perform(post(TENANT_CREATE)
        .contentType(APPLICATION_JSON)
        // .content(buildLiveEventMessage(DIRECTMESSAGE, asList("1", "2"), null))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }


}
