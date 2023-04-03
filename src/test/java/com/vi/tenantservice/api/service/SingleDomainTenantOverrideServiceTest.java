package com.vi.tenantservice.api.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleDomainTenantOverrideServiceTest {

  @Mock TranslationService translationService;

  @Mock TenantConverter tenantConverter;

  @InjectMocks SingleDomainTenantOverrideService singleDomainTenantOverrideService;

  @Test
  void overridePrivacyAndCertainSettings_Should_OverridePrivacyAndSettingFromMainTenant() {

    // given
    var mainTenant = new TenantEntity();
    var actualTenant = new TenantEntity();
    when(translationService.getCurrentLanguageContext()).thenReturn("de");
    when(tenantConverter.toRestrictedTenantDTO(mainTenant, "de"))
        .thenReturn(restrictedDTO("main privacy", LocalDateTime.now().minusDays(1), false));
    LocalDateTime actualPrivacyChangedDate = LocalDateTime.now();
    when(tenantConverter.toRestrictedTenantDTO(actualTenant, "de"))
        .thenReturn(restrictedDTO("actual privacy", actualPrivacyChangedDate, true));

    // when
    RestrictedTenantDTO restrictedTenantDTO =
        singleDomainTenantOverrideService.overridePrivacyAndCertainSettings(
            mainTenant, actualTenant);

    // then
    assertThat(restrictedTenantDTO.getContent().getPrivacy()).isEqualTo("actual privacy");
    assertThat(restrictedTenantDTO.getContent().getDataPrivacyConfirmation())
        .isEqualTo(actualPrivacyChangedDate);
    assertThat(restrictedTenantDTO.getSettings().getFeatureAttachmentUploadDisabled()).isTrue();
  }

  private static RestrictedTenantDTO restrictedDTO(
      String privacy, LocalDateTime privacyChangedDate, boolean featureAttachmentUploadDisabled) {
    return new RestrictedTenantDTO()
        .content(new Content().privacy(privacy).dataPrivacyConfirmation(privacyChangedDate))
        .settings(new Settings().featureAttachmentUploadDisabled(featureAttachmentUploadDisabled));
  }
}
