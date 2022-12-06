package com.vi.tenantservice.api.validation;

import com.google.common.collect.Lists;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.Translation;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TenantInputSanitizerTest {

  private static final String LINK_CONTENT = "<a href=\"http://onlineberatung.net\">content</a>further content";
  private static final String IMAGE_CONTENT = "<img src=\"http://onlineberatung.net/images/test.png\" width=\"272\" height=\"92\" />";

  @InjectMocks
  TenantInputSanitizer tenantInputSanitizer;

  @Mock
  InputSanitizer inputSanitizer;

  @Test
  void sanitize_Should_sanitizeTenantDTO() {
    // given
    EasyRandom generator = new EasyRandom();
    MultilingualTenantDTO tenantDTO = generator.nextObject(MultilingualTenantDTO.class);
    tenantDTO.getContent().setImpressum(getDefaultTranslations("impressum"));
    tenantDTO.getContent().setClaim(getDefaultTranslations("claim"));
    tenantDTO.getContent().setPrivacy(getDefaultTranslations("privacy"));
    tenantDTO.getContent().setTermsAndConditions(getDefaultTranslations("terms and conditions"));

    // when
    MultilingualTenantDTO sanitized = tenantInputSanitizer.sanitize(tenantDTO);

    // then
    verifyNeededSanitizationsAreCalled(tenantDTO);
    assertNonSanitizableFieldsHaveSameValues(tenantDTO, sanitized);
  }

  @Test
  void sanitize_Should_sanitizeAndAllowLinksForContentInTenantDTO() {
    // given
    EasyRandom generator = new EasyRandom();
    MultilingualTenantDTO tenantDTO = generator.nextObject(MultilingualTenantDTO.class);
    tenantDTO.getContent().setTermsAndConditions(getDefaultTranslations(LINK_CONTENT));
    tenantDTO.getContent().setPrivacy(getDefaultTranslations(LINK_CONTENT));
    tenantDTO.getContent().setImpressum(getDefaultTranslations(LINK_CONTENT));
    TenantInputSanitizer nonMockedTenantInputSanitizer = new TenantInputSanitizer(new InputSanitizer());
    // when
    MultilingualTenantDTO sanitized = nonMockedTenantInputSanitizer.sanitize(tenantDTO);

    // then
    assertThat(sanitized.getContent().getTermsAndConditions()).isEqualTo(getDefaultTranslations(LINK_CONTENT));
    assertThat(sanitized.getContent().getPrivacy()).isEqualTo(getDefaultTranslations(LINK_CONTENT));
    assertThat(sanitized.getContent().getImpressum()).isEqualTo(getDefaultTranslations(LINK_CONTENT));
  }

  private List<Translation> getDefaultTranslations(String content) {
    return Lists.newArrayList(new Translation().lang("de").value(content));
  }

  @Test
  void sanitize_Should_sanitizeAndAllowImageSrcForContentInTenantDTO() {
    // given
    EasyRandom generator = new EasyRandom();
    MultilingualTenantDTO tenantDTO = generator.nextObject(MultilingualTenantDTO.class);
    tenantDTO.getContent().setTermsAndConditions(getDefaultTranslations(IMAGE_CONTENT));
    tenantDTO.getContent().setPrivacy(getDefaultTranslations(IMAGE_CONTENT));
    tenantDTO.getContent().setImpressum(getDefaultTranslations(IMAGE_CONTENT));
    TenantInputSanitizer nonMockedTenantInputSanitizer = new TenantInputSanitizer(new InputSanitizer());
    // when
    MultilingualTenantDTO sanitized = nonMockedTenantInputSanitizer.sanitize(tenantDTO);

    // then
    assertThat(sanitized.getContent().getTermsAndConditions()).isEqualTo(getDefaultTranslations(IMAGE_CONTENT));
    assertThat(sanitized.getContent().getPrivacy()).isEqualTo(getDefaultTranslations(IMAGE_CONTENT));
    assertThat(sanitized.getContent().getImpressum()).isEqualTo(getDefaultTranslations(IMAGE_CONTENT));
  }

  private void verifyNeededSanitizationsAreCalled(MultilingualTenantDTO tenantDTO) {
    verify(inputSanitizer).sanitize(tenantDTO.getName());
    verify(inputSanitizer).sanitize(tenantDTO.getSubdomain());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getLogo());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getFavicon());
    verify(inputSanitizer).sanitizeAllowingFormatting(Mockito.anyString());
    verify(inputSanitizer, Mockito.times(3)).sanitizeAllowingFormattingAndLinks(Mockito.anyString());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getPrimaryColor());
    verify(inputSanitizer).sanitize(tenantDTO.getTheming().getSecondaryColor());
    verifyNoMoreInteractions(inputSanitizer);
  }

  private void assertNonSanitizableFieldsHaveSameValues(MultilingualTenantDTO tenantDTO, MultilingualTenantDTO sanitized) {
    assertThat(tenantDTO.getId()).isEqualTo(sanitized.getId());
    assertThat(tenantDTO.getCreateDate()).isEqualTo(sanitized.getCreateDate());
    assertThat(tenantDTO.getUpdateDate()).isEqualTo(sanitized.getUpdateDate());
    assertThat(tenantDTO.getLicensing().getAllowedNumberOfUsers()).isEqualTo(
        sanitized.getLicensing().getAllowedNumberOfUsers());
  }

}