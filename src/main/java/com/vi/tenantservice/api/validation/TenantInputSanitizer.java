package com.vi.tenantservice.api.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason;
import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.model.Theming;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TenantInputSanitizer {

  private final @NonNull InputSanitizer inputSanitizer;

  public TenantDTO sanitize(TenantDTO input) {
    log.info("Sanitizing input DTO");
    TenantDTO output = copyNotSanitizedAttributes(input);
    output.setName(inputSanitizer.sanitize(input.getName()));
    output.setSubdomain(inputSanitizer.sanitize(input.getSubdomain()));
    sanitizeTheming(input, output);
    sanitizeContent(input, output);
    sanitizeSettings(input, output);
    return output;
  }

  private void sanitizeSettings(TenantDTO input, TenantDTO output) {
    try {
      sanitizeSettingsIfValueNotNull(input, output);
    } catch (JsonProcessingException ex) {
      throw new TenantValidationException(HttpStatusExceptionReason.INVALID_SETTINGS_VALUE,
          HttpStatus.BAD_REQUEST);
    }
  }

  private void sanitizeSettingsIfValueNotNull(TenantDTO input, TenantDTO output) throws JsonProcessingException {
    if (input.getSettings() != null) {
      TenantSettings tenantSettings = tryDeserializeToJson(input.getSettings());
      output.settings(new ObjectMapper().writeValueAsString(tenantSettings));
    }
  }

  private TenantSettings tryDeserializeToJson(String settings) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(settings, TenantSettings.class);
  }

  private TenantDTO copyNotSanitizedAttributes(TenantDTO input) {
    TenantDTO output = new TenantDTO();
    output.setId(input.getId());
    output.setCreateDate(input.getCreateDate());
    output.setUpdateDate(input.getUpdateDate());
    output.setContent(new Content());
    output.setTheming(new Theming());
    output.setLicensing(input.getLicensing());
    return output;
  }

  private void sanitizeTheming(TenantDTO input, TenantDTO output) {
    Theming theming = input.getTheming();
    if (theming != null) {
      output.getTheming().setLogo(inputSanitizer.sanitize(theming.getLogo()));
      output.getTheming().setFavicon(inputSanitizer.sanitize(theming.getFavicon()));
      output.getTheming().setPrimaryColor(inputSanitizer.sanitize(theming.getPrimaryColor()));
      output.getTheming().setSecondaryColor(inputSanitizer.sanitize(theming.getSecondaryColor()));
    }
  }

  private void sanitizeContent(TenantDTO input, TenantDTO output) {
    Content content = input.getContent();
    if (content != null) {
      output.getContent()
          .setImpressum(inputSanitizer.sanitizeAllowingFormattingAndLinks(content.getImpressum()));
      output.getContent()
          .setClaim(inputSanitizer.sanitizeAllowingFormatting(content.getClaim()));
      output.getContent()
          .setPrivacy(inputSanitizer.sanitizeAllowingFormattingAndLinks(content.getPrivacy()));
      output.getContent()
          .setTermsAndConditions(inputSanitizer.sanitizeAllowingFormattingAndLinks(content.getTermsAndConditions()));
    }
  }
}
