package com.vi.tenantservice.api.validation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.vi.tenantservice.api.model.TenantDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantInputSanitizerTest {

  private static final String LINK_CONTENT = "<a href=\"http://onlineberatung.net\">content</a>further content";

  @InjectMocks
  TenantInputSanitizer tenantInputSanitizer;

  @Mock
  InputSanitizer inputSanitizer;

  @Test
  void sanitize_Should_sanitizeTenantDTO() {
    // given
    EasyRandom generator = new EasyRandom();
    TenantDTO tenantDTO = generator.nextObject(TenantDTO.class);

    // when
    TenantDTO sanitized = tenantInputSanitizer.sanitize(tenantDTO);

    // then
    verifyNeededSanitizationsAreCalled(tenantDTO);
    assertNonSanitizableFieldsHaveSameValues(tenantDTO, sanitized);
  }

  @Test
  void sanitize_Should_sanitizeAndAllowLinksForContentInTenantDTO() {
    // given
    EasyRandom generator = new EasyRandom();
    TenantDTO tenantDTO = generator.nextObject(TenantDTO.class);
    tenantDTO.getContent().setTermsAndConditions(LINK_CONTENT);
    tenantDTO.getContent().setPrivacy(LINK_CONTENT);
    tenantDTO.getContent().setImpressum(LINK_CONTENT);
    TenantInputSanitizer nonMockedTenantInputSanitizer = new TenantInputSanitizer(new InputSanitizer());
    // when
    TenantDTO sanitized = nonMockedTenantInputSanitizer.sanitize(tenantDTO);

    // then
    assertThat(sanitized.getContent().getTermsAndConditions()).contains(LINK_CONTENT);
    assertThat(sanitized.getContent().getPrivacy()).contains(LINK_CONTENT);
    assertThat(sanitized.getContent().getImpressum()).contains(LINK_CONTENT);
  }

  private void verifyNeededSanitizationsAreCalled(TenantDTO tenantDTO) {
    verify(inputSanitizer).sanitize(tenantDTO.getName());
    verify(inputSanitizer).sanitize(tenantDTO.getSubdomain());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getLogo());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getFavicon());
    verify(inputSanitizer).sanitizeAllowingFormatting(tenantDTO.getContent().getClaim());
    verify(inputSanitizer).sanitizeAllowingFormattingAndLinks(tenantDTO.getContent().getImpressum());
    verify(inputSanitizer).sanitizeAllowingFormattingAndLinks(tenantDTO.getContent().getPrivacy());
    verify(inputSanitizer).sanitizeAllowingFormattingAndLinks(tenantDTO.getContent().getTermsAndConditions());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getPrimaryColor());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getSecondaryColor());
    verifyNoMoreInteractions(inputSanitizer);
  }

  private void assertNonSanitizableFieldsHaveSameValues(TenantDTO tenantDTO, TenantDTO sanitized) {
    assertThat(tenantDTO.getId()).isEqualTo(sanitized.getId());
    assertThat(tenantDTO.getCreateDate()).isEqualTo(sanitized.getCreateDate());
    assertThat(tenantDTO.getUpdateDate()).isEqualTo(sanitized.getUpdateDate());
    assertThat(tenantDTO.getLicensing().getAllowedNumberOfUsers()).isEqualTo(
        sanitized.getLicensing().getAllowedNumberOfUsers());
  }

}