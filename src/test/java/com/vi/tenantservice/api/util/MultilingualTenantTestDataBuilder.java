package com.vi.tenantservice.api.util;

import com.google.common.collect.Lists;
import com.vi.tenantservice.api.model.Licensing;
import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantMultilingualDTO;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.api.model.Translation;

import java.util.List;

public class MultilingualTenantTestDataBuilder {

  private static final String SUBDOMAIN = "subdomain";
  private static final String NAME = "name";
  private static final long ID = 1L;
  private static final String IMPRESSUM = "Impressum";
  private static final String CLAIM = "claim";
  private static final String SECONDARY_COLOR = "secondaryColor";
  private static final String PRIMARY_COLOR = "primary color";
  private static final String FAVICON = "favicon";
  private static final String LOGO = "logo";
  private static final int ALLOWED_NUMBER_OF_USERS = 2000;
  private static final String PRIVACY = "privacy";
  private static final String TERMS_AND_CONDITIONS = "termsandconditions";

  TenantMultilingualDTO tenantMultilingualDTO = new TenantMultilingualDTO();

  public MultilingualTenantTestDataBuilder tenantDTO() {
    tenantMultilingualDTO = new TenantMultilingualDTO();
    tenantMultilingualDTO.setId(ID);
    tenantMultilingualDTO.setName(NAME);
    tenantMultilingualDTO.setSubdomain(SUBDOMAIN);
    return this;
  }

  public MultilingualTenantTestDataBuilder withId(Long id) {
    tenantMultilingualDTO.setId(id);
    return this;
  }

  public MultilingualTenantTestDataBuilder withName(String name) {
    tenantMultilingualDTO.setName(name);
    return this;
  }

  public MultilingualTenantTestDataBuilder withSubdomain(String subdomain) {
    tenantMultilingualDTO.setSubdomain(subdomain);
    return this;
  }

  public MultilingualTenantTestDataBuilder withSettingTopicsInRegistrationEnabled(
      boolean topicsInRegistrationEnabled) {
    tenantMultilingualDTO.setSettings(getSettings()
        .topicsInRegistrationEnabled(topicsInRegistrationEnabled));
    return this;
  }

  public MultilingualTenantTestDataBuilder withSettingActiveLanguages(
          List<String> activeLanguages) {
    tenantMultilingualDTO.setSettings(getSettings()
            .topicsInRegistrationEnabled(true)
            .activeLanguages(activeLanguages)
    );
    return this;
  }

  public MultilingualTenantTestDataBuilder withContent() {
    tenantMultilingualDTO.setContent(content());
    return this;
  }

  public MultilingualTenantTestDataBuilder withTheming() {
    tenantMultilingualDTO.setTheming(theming());
    return this;
  }

  public MultilingualTenantTestDataBuilder withLicensing() {
    tenantMultilingualDTO.setLicensing(licensing());
    return this;
  }

  public MultilingualTenantTestDataBuilder withSettings() {
    tenantMultilingualDTO.setSettings(
        getSettings()
                .topicsInRegistrationEnabled(true)
                .activeLanguages(Lists.newArrayList("de", "en"))
    );
    return this;
  }

  private static Settings getSettings() {
    return new Settings()
            .featureTopicsEnabled(true)
            .featureDemographicsEnabled(true)
            .featureAppointmentsEnabled(true)
            .featureStatisticsEnabled(true)
            .featureTopicsEnabled(true)
            .featureGroupChatV2Enabled(true)
            .featureToolsEnabled(true)
            .featureToolsOICDToken("1234")
            .featureAttachmentUploadDisabled(false);
  }

  private Licensing licensing() {
    Licensing licensing = new Licensing();
    licensing.setAllowedNumberOfUsers(ALLOWED_NUMBER_OF_USERS);
    return licensing;
  }

  public TenantMultilingualDTO build() {
    return tenantMultilingualDTO;
  }

  private Theming theming() {
    Theming theming = new Theming();
    theming.setSecondaryColor(SECONDARY_COLOR);
    theming.setPrimaryColor(PRIMARY_COLOR);
    theming.setFavicon(FAVICON);
    theming.setLogo(LOGO);
    return theming;
  }

  private MultilingualContent content() {
    MultilingualContent content = new MultilingualContent();
    content.setImpressum(defaultTranslations(IMPRESSUM));
    content.setClaim(defaultTranslations(CLAIM));
    content.setPrivacy(defaultTranslations(PRIVACY));
    content.setTermsAndConditions(defaultTranslations(TERMS_AND_CONDITIONS));
    return content;
  }

  private List<Translation> defaultTranslations(String content) {
    return Lists.newArrayList(new Translation().lang("de").value(content), new Translation().lang("en").value("en_"+content));
  }

  public String jsonify() {
    TenantMultilingualDTO build = build();
    return JsonConverter.convertToJson(build);
  }

  public MultilingualTenantTestDataBuilder withContent(String impressum, String claim) {
    MultilingualContent content = new MultilingualContent();
    content.setImpressum(defaultTranslations(impressum));
    content.setClaim(defaultTranslations(claim));
    tenantMultilingualDTO.setContent(content);
    return this;
  }
}
