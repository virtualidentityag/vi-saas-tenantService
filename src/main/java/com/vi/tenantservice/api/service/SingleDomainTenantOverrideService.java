package com.vi.tenantservice.api.service;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SingleDomainTenantOverrideService {

  private final @NonNull TenantConverter tenantConverter;
  private final @NonNull TranslationService translationService;

  public RestrictedTenantDTO overridePrivacyAndCertainSettings(
      TenantEntity mainTenant, TenantEntity actualTenant) {
    String lang = translationService.getCurrentLanguageContext();

    RestrictedTenantDTO mainTenantRestrictedDTO = getRestrictedTenantDTO(mainTenant, lang);
    RestrictedTenantDTO overridingRestrictedTenantDTO = getRestrictedTenantDTO(actualTenant, lang);

    overrideContent(mainTenantRestrictedDTO, overridingRestrictedTenantDTO);
    overrideSettings(mainTenantRestrictedDTO, overridingRestrictedTenantDTO);
    return mainTenantRestrictedDTO;
  }

  private static void overrideSettings(
      RestrictedTenantDTO mainTenantRestrictedDTO,
      RestrictedTenantDTO overridingRestrictedTenantDTO) {
    mainTenantRestrictedDTO
        .getSettings()
        .setFeatureAttachmentUploadDisabled(
            overridingRestrictedTenantDTO.getSettings().getFeatureAttachmentUploadDisabled());
  }

  private RestrictedTenantDTO getRestrictedTenantDTO(TenantEntity mainTenant, String lang) {
    return tenantConverter.toRestrictedTenantDTO(mainTenant, lang);
  }

  private static void overrideContent(
      RestrictedTenantDTO restrictedTenantDTO, RestrictedTenantDTO overridingRestrictedTenantDTO) {
    if (overridingRestrictedTenantDTO.getContent() != null) {
      restrictedTenantDTO
          .getContent()
          .dataPrivacyConfirmation(overridingRestrictedTenantDTO.getContent()
              .getDataPrivacyConfirmation())
          .setPrivacy(overridingRestrictedTenantDTO.getContent().getPrivacy());
    }
  }
}
