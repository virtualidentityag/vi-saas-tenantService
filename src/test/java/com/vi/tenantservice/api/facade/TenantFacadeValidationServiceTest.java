package com.vi.tenantservice.api.facade;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantFacadeValidationServiceTest {

  @InjectMocks
  TenantFacadeValidationService tenantFacadeValidationService;

  @Test
  void validate_ShouldPassValidation_When_SettingValueIsCorrect() {
    // given
    String settings = JsonConverter.convertToJson(new TenantSettings());
    var tenantWithValidSettings = new TenantDTO().settings(settings);

    // when, then
    tenantFacadeValidationService.validate(tenantWithValidSettings);
  }

  @Test
  void validate_ShouldPassValidation_When_SettingValueIsNull() {
    // given
    var tenantWithValidSettings = new TenantDTO().settings(null);

    // when, then
    tenantFacadeValidationService.validate(tenantWithValidSettings);
  }

  @Test
  void validate_ShouldNotPassValidation_When_SettingValueIsEmpty() {
    // given
    var tenantWithValidSettings = new TenantDTO().settings("");

    // when, then
    assertThrows(TenantValidationException.class, () ->
        tenantFacadeValidationService.validate(tenantWithValidSettings));
  }

  @Test
  void validate_ShouldNotPassValidation_When_SettingValueContainsNotValidJsonContent() {
    // given
    var tenantWithValidSettings = new TenantDTO().settings("non json content");

    // when, then
    assertThrows(TenantValidationException.class, () ->
        tenantFacadeValidationService.validate(tenantWithValidSettings));
  }

  @Test
  void validate_ShouldPassValidation_When_SettingValueContainsValidJsonContentAndSomeAdditionalSettings() {
    // given
    var tenantWithValidSettings = new TenantDTO().settings("{\"topicsInRegistrationEnabled\":false,\"featureTopicsEnabled\":false,\"featureDemographicsEnabled\":false, \"new setting\":true}");

    // when, then
    tenantFacadeValidationService.validate(tenantWithValidSettings);
  }

}