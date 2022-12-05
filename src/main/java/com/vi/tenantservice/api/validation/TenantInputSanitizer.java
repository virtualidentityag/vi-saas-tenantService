package com.vi.tenantservice.api.validation;

import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.TenantMultilingualDTO;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.api.model.Translation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class TenantInputSanitizer {

  private final @NonNull InputSanitizer inputSanitizer;

  public TenantMultilingualDTO sanitize(TenantMultilingualDTO input) {
    log.info("Sanitizing input DTO");
    TenantMultilingualDTO output = copyNotSanitizedAttributes(input);
    output.setName(inputSanitizer.sanitize(input.getName()));
    output.setSubdomain(inputSanitizer.sanitize(input.getSubdomain()));
    sanitizeTheming(input, output);
    sanitizeContent(input, output);
    return output;
  }

  private TenantMultilingualDTO copyNotSanitizedAttributes(TenantMultilingualDTO input) {
    TenantMultilingualDTO output = new TenantMultilingualDTO();
    output.setId(input.getId());
    output.setCreateDate(input.getCreateDate());
    output.setUpdateDate(input.getUpdateDate());
    output.setContent(new MultilingualContent());
    output.setTheming(new Theming());
    output.setLicensing(input.getLicensing());
    output.setSettings(input.getSettings());
    return output;
  }

  private void sanitizeTheming(TenantMultilingualDTO input, TenantMultilingualDTO output) {
    Theming theming = input.getTheming();
    if (theming != null) {
      output.getTheming().setLogo(inputSanitizer.sanitize(theming.getLogo()));
      output.getTheming().setFavicon(inputSanitizer.sanitize(theming.getFavicon()));
      output.getTheming().setPrimaryColor(inputSanitizer.sanitize(theming.getPrimaryColor()));
      output.getTheming().setSecondaryColor(inputSanitizer.sanitize(theming.getSecondaryColor()));
    }
  }

  private void sanitizeContent(TenantMultilingualDTO input, TenantMultilingualDTO output) {
    var content = input.getContent();
    if (content != null) {
      output.getContent()
              .setImpressum(sanitizeAllTranslations(content.getImpressum()));
      output.getContent()
              .setClaim(sanitizeAllTranslationsAllowingFormatting(content.getClaim()));
      output.getContent()
              .setPrivacy(sanitizeAllTranslations(content.getPrivacy()));
      output.getContent()
              .setTermsAndConditions(sanitizeAllTranslations(content.getTermsAndConditions()));
    }
  }

  private List<Translation> sanitizeAllTranslationsAllowingFormatting(List<Translation> translations) {
    if (translations != null) {
      for (Translation translation : translations) {
        translation.setValue(inputSanitizer.sanitizeAllowingFormatting(translation.getValue()));
      }
    }
    return translations;
  }

  private List<Translation> sanitizeAllTranslations(List<Translation> translations) {
    if (translations != null) {
      translations.stream().forEach(translation -> translation.setValue(inputSanitizer.sanitizeAllowingFormattingAndLinks(translation.getValue())));
    }
    return translations;
  }
}
