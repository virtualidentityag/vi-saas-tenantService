package com.vi.tenantservice.api.facade;

import com.google.common.collect.Sets;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.vi.tenantservice.api.model.TenantSetting.*;
import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class TenantFacadeChangeDetectionServiceTest {

  @InjectMocks
  TenantFacadeChangeDetectionService tenantFacadeChangeDetectionService;

  @Test
  void determineChangedSettings_Should_NotDetectAnyChanges_When_InputDTOIsNull() {
    Object o = null;
    assertThat(Sets.newHashSet(o)).isEqualTo(Sets.newHashSet(o));
    // given
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO();
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_NotDetectAnyChanges_When_InputDTOIsEmptyAndEntitySettingsAreNull() {
    // given
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(new Settings());
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_DetectChanges_When_InputDTOContainsChangesToDefaultValues() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true).featureGroupChatV2Enabled(true);
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);
    TenantEntity existingTenant = new TenantEntity();

    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .contains(FEATURE_DEMOGRAPHICS_ENABLED, FEATURE_TOPICS_ENABLED,
            ENABLE_TOPICS_IN_REGISTRATION, FEATURE_GROUP_CHAT_V2_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_Not_DetectChanges_When_InputDTOContainsSameSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true).featureGroupChatV2Enabled(true);
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureDemographicsEnabled(true).featureTopicsEnabled(true)
        .topicsInRegistrationEnabled(true).featureGroupChatV2Enabled(true).build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant)).isEmpty();
  }

  @Test
  void determineChangedSettings_Should_DetectStatisticsFeatureChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureStatisticsEnabled(false);
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

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
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

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
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

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
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

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

  @Test
  void determineChangedSettings_Should_DetectGroupChatV2FeatureChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureGroupChatV2Enabled(false);
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureGroupChatV2Enabled(true)
        .build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .containsOnly(FEATURE_GROUP_CHAT_V2_ENABLED);
  }

  @Test
  void determineChangedSettings_Should_DetectFeatureAttachmentUploadDisabledChanges_When_InputDTOContainsDifferentSettingsAsEntity() {
    // given
    Settings settings = new Settings().featureAttachmentUploadDisabled(true);
    MultilingualTenantDTO sanitizedTenantDTO = new MultilingualTenantDTO().settings(settings);

    TenantSettings existingTenantSettings = TenantSettings.builder()
        .featureAttachmentUploadDisabled(false)
        .build();
    TenantEntity existingTenant = TenantEntity.builder()
        .settings(convertToJson(existingTenantSettings)).build();
    // when, then
    assertThat(tenantFacadeChangeDetectionService.determineChangedSettings(sanitizedTenantDTO,
        existingTenant))
        .containsOnly(FEATURE_ATTACHMENT_UPLOAD_DISABLED);
  }

}