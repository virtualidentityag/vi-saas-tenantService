package com.vi.tenantservice.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantSettings {

  boolean featureStatisticsEnabled;
  boolean featureTopicsEnabled;
  boolean topicsInRegistrationEnabled;
  boolean featureDemographicsEnabled;
  boolean featureAppointmentsEnabled;
  boolean featureGroupChatV2Enabled;
  boolean featureToolsEnabled;
  boolean featureAttachmentUploadDisabled;

  String featureToolsOIDCToken;
  List<String> activeLanguages;
}
