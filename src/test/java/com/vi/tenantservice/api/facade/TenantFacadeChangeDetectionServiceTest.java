package com.vi.tenantservice.api.facade;

import static com.vi.tenantservice.api.model.TenantSetting.ENABLE_TOPICS_IN_REGISTRATION;
import static com.vi.tenantservice.api.model.TenantSetting.FEATURE_APPOINTMENTS_ENABLED;
import static com.vi.tenantservice.api.model.TenantSetting.FEATURE_DEMOGRAPHICS_ENABLED;
import static com.vi.tenantservice.api.model.TenantSetting.FEATURE_MULTITENANCY_ENABLED;
import static com.vi.tenantservice.api.model.TenantSetting.FEATURE_STATISTICS_ENABLED;
import static com.vi.tenantservice.api.model.TenantSetting.FEATURE_TOPICS_ENABLED;
import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantFacadeChangeDetectionServiceTest {

  @InjectMocks
  TenantFacadeChangeDetectionService tenantFacadeChangeDetectionService;

  @Test
  void determineChangedSettings_Should_NotDetectAnyChanges_When_InputDTOIsNull() {
    // given
    TenantDTO sanitizedTenantDTO = new TenantDTO();
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_NotDetectAnyChanges_When_InputDTOIsEmptyAndEntitySettingsAreNull() {
    // given
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(new Settings());
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_DetectChanges_When_InputDTOContainsChangesToDefaultValues() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .contains(FEATURE_DEMOGRAPHICS_ENABLED, FEATURE_TOPICS_ENABLED,
            ENABLE_TOPICS_IN_REGISTRATION);
  }

  @Test
  void determineChangedSettings_Should_Not_DetectChanges_When_InputDTOContainsSameSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder().featureDemographicsEnabled(true)
        .featureTopicsEnabled(true).topicsInRegistrationEnabled(true).build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_DetectMultitenancyFeatureChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureMultitenancyEnabled(false)
        .featureDemographicsEnabled(true)
        .featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureDemographicsEnabled(true)
        .featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true)
        .featureMultitenancyEnabled(true)
        .build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .containsOnly(FEATURE_MULTITENANCY_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_DetectStatisticsFeatureChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureStatisticsEnabled(false);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureStatisticsEnabled(true)
        .build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .containsOnly(FEATURE_STATISTICS_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_DetectAppointmentFeatureChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureAppointmentsEnabled(false);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureAppointmentsEnabled(true)
        .build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .containsOnly(FEATURE_APPOINTMENTS_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_DetectChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(false).featureTopicsEnabled(false)
        .topicsInRegistrationEnabled(true);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureDemographicsEnabled(true)
        .featureTopicsEnabled(true).topicsInRegistrationEnabled(true).build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .contains(FEATURE_DEMOGRAPHICS_ENABLED, FEATURE_TOPICS_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_DetectEnableTopicInRegistrationSettingChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(false);
    TenantDTO sanitizedTenantDTO = new TenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureDemographicsEnabled(true)
        .featureTopicsEnabled(true).topicsInRegistrationEnabled(true).build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .contains(ENABLE_TOPICS_IN_REGISTRATION);
  }

}