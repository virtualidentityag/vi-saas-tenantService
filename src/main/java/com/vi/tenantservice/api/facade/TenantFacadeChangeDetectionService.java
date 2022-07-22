package com.vi.tenantservice.api.facade;

import com.google.common.collect.Lists;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantSetting;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.util.JsonConverter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TenantFacadeChangeDetectionService {

  public List<TenantSetting> determineChangedSettings(TenantDTO sanitizedTenantDTO, TenantEntity existingTenant) {
    Settings inputSettings = sanitizedTenantDTO.getSettings();
    if (inputSettings != null) {
      TenantSettings existingSettingsToCompare = getExistingTenantSettings(
          existingTenant);
      return getChangedTenantSettings(inputSettings, existingSettingsToCompare);
    } else {
      return Lists.newArrayList();
    }
  }

  private List<TenantSetting> getChangedTenantSettings(Settings inputSettings,
      TenantSettings existingSettingsToCompare) {
    List<TenantSetting> resultList = Lists.newArrayList();
    if (isChanged(inputSettings.getFeatureDemographicsEnabled(),
        existingSettingsToCompare.isFeatureDemographicsEnabled())) {
      resultList.add(TenantSetting.FEATURE_DEMOGRAPHICS_ENABLED);
    }
    if (isChanged(inputSettings.getFeatureTopicsEnabled(),
        existingSettingsToCompare.isFeatureTopicsEnabled())) {
      resultList.add(TenantSetting.FEATURE_TOPICS_ENABLED);
    }
    if (isChanged(inputSettings.getTopicsInRegistrationEnabled(),
        existingSettingsToCompare.isTopicsInRegistrationEnabled())) {
      resultList.add(TenantSetting.ENABLE_TOPICS_IN_REGISTRATION);
    }
    if (isChanged(inputSettings.getFeatureStatisticsEnabled(),
        existingSettingsToCompare.isFeatureStatisticsEnabled())) {
      resultList.add(TenantSetting.FEATURE_STATISTICS_ENABLED);
    }
    if (isChanged(inputSettings.getFeatureAppointmentsEnabled(),
        existingSettingsToCompare.isFeatureAppointmentsEnabled())) {
      resultList.add(TenantSetting.FEATURE_APPOINTMENTS_ENABLED);
    }
    return resultList;
  }

  private boolean isChanged(Boolean inputSettings, boolean existingSettingsToCompare) {
    return nullAsFalse(inputSettings)
        != existingSettingsToCompare;
  }

  private TenantSettings getExistingTenantSettings(TenantEntity existingTenant) {
    TenantSettings existingSettingsToCompare;
    if (existingTenant.getSettings() == null) {
      existingSettingsToCompare = new TenantSettings();
    } else {
      existingSettingsToCompare = JsonConverter.convertFromJson(existingTenant.getSettings());
    }
    return existingSettingsToCompare;
  }

  boolean nullAsFalse(Boolean value) {
    return value != null ? value : false;
  }
}
