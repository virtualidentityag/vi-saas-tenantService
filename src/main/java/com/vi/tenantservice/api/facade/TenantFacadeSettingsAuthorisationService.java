package com.vi.tenantservice.api.facade;

import com.vi.tenantservice.api.model.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantFacadeSettingsAuthorisationService {

  public void authorizeSettings(TenantDTO tenantDTO) {

    if (tenantHasSettings(tenantDTO)) {

      //TODO tkuzynow implement
    }

  }

  private boolean tenantHasSettings(TenantDTO tenantDTO) {
    return tenantDTO.getSettings() != null;
  }
}
