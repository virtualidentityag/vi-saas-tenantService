package com.vi.tenantservice.api.config.apiclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.vi.tenantservice.useradminservice.generated.web.AdminUserControllerApi;
import com.vi.tenantservice.useradminservice.generated.ApiClient;

@Component
public class UserAdminServiceApiControllerFactory {

  @Value("${user.service.api.url}")
  private String userServiceApiUrl;

  @Autowired
  private RestTemplate restTemplate;

  public AdminUserControllerApi createControllerApi() {
    var apiClient = new ApiClient(restTemplate).setBasePath(this.userServiceApiUrl);
    return new AdminUserControllerApi(apiClient);
  }
}
