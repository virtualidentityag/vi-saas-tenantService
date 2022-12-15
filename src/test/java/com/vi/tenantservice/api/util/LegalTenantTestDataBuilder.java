package com.vi.tenantservice.api.util;

import com.vi.tenantservice.api.model.LegalTenantDTO;
import com.vi.tenantservice.api.model.MultilingualLegalContent;

import java.util.HashMap;
import java.util.Map;

public class LegalTenantTestDataBuilder {

  private static final long ID = 1L;
  private static final String IMPRESSUM = "Impressum";
  private static final String PRIVACY = "privacy";
  private static final String TERMS_AND_CONDITIONS = "termsandconditions";

  LegalTenantDTO legalTenantDTO = new LegalTenantDTO();

  public LegalTenantTestDataBuilder tenantDTO() {
    legalTenantDTO = new LegalTenantDTO();
    legalTenantDTO.setId(ID);
    return this;
  }

  public LegalTenantTestDataBuilder withId(Long id) {
    legalTenantDTO.setId(id);
    return this;
  }

  public LegalTenantTestDataBuilder withContent() {
    legalTenantDTO.setContent(content());
    return this;
  }

  public LegalTenantDTO build() {
    return legalTenantDTO;
  }



  private MultilingualLegalContent content() {
    MultilingualLegalContent content = new MultilingualLegalContent();
    content.setImpressum(defaultTranslations(IMPRESSUM));
    content.setPrivacy(defaultTranslations(PRIVACY));
    content.setTermsAndConditions(defaultTranslations(TERMS_AND_CONDITIONS));
    content.setConfirmPrivacy(true);
    content.setConfirmTermsAndConditions(true);
    return content;
  }

  private Map<String, String> defaultTranslations(String content) {
    var map = new HashMap<String, String>();
    map.put("de", content);
    return map;
  }

  public String jsonify() {
    LegalTenantDTO build = build();
    return JsonConverter.convertToJson(build);
  }

  public LegalTenantTestDataBuilder withImpressum(String impressum) {
    MultilingualLegalContent content = new MultilingualLegalContent();
    content.setImpressum(defaultTranslations(impressum));
    legalTenantDTO.setContent(content);
    return this;
  }
}
