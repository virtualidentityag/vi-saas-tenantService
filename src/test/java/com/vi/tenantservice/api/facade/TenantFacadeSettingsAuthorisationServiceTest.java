package com.vi.tenantservice.api.facade;

import com.vi.tenantservice.api.model.TenantDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantFacadeSettingsAuthorisationServiceTest {

  @InjectMocks
  TenantFacadeSettingsAuthorisationService tenantFacadeValidationService;

  @Test
  void validate_ShouldPassAuthorisation_When_SettingValueIsNull() {
    // given
    var ten = new TenantDTO().settings(null);

    // when, then
    tenantFacadeValidationService.authorizeSettings(ten);
  }


}