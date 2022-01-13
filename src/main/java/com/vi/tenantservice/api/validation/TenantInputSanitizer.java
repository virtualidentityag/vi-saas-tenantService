package com.vi.tenantservice.api.validation;

import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.Theming;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    return output;
  }

  private TenantDTO copyNotSanitizedAttributes(TenantDTO input) {
    TenantDTO output = new TenantDTO();
    output.setId(input.getId());
    output.setCreateDate(input.getCreateDate());
    output.setCreateDate(input.getUpdateDate());
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
          .setImpressum(inputSanitizer.sanitizeAllowingFormatting(content.getImpressum()));
      output.getContent()
          .setClaim(inputSanitizer.sanitizeAllowingFormatting(content.getClaim()));
    }
  }
}
