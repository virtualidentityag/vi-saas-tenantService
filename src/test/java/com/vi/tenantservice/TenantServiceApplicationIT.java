package com.vi.tenantservice;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;

@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.profiles.active=testing")
class TenantServiceApplicationIT {

}
