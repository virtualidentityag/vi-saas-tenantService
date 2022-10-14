package com.vi.tenantservice.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantSettings {

  boolean featureMultitenancyEnabled;
  boolean featureStatisticsEnabled;
  boolean featureTopicsEnabled;
  boolean topicsInRegistrationEnabled;
  boolean featureDemographicsEnabled;
  boolean featureAppointmentsEnabled;
  boolean featureGroupChatV2Enabled;
  boolean featureToolsEnabled;
  boolean featureAttachmentUploadDisabled;

  String featureToolsOIDCToken;
}
