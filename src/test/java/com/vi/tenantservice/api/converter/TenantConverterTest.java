package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.Translation;
import com.vi.tenantservice.api.util.MultilingualTenantTestDataBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TenantConverterTest {

  TenantConverter tenantConverter = new TenantConverter();

  @Test
  void toEntity_should_convertToEntityAndBackToDTO() {
    // given
    MultilingualTenantDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();

    // when
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // then
    TenantDTO converted = tenantConverter.toDTO(entity, "de");
    assertThat(converted.getId()).isEqualTo(tenantDTO.getId());
    assertThat(converted.getName()).isEqualTo(tenantDTO.getName());
    assertThat(converted.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(converted.getLicensing()).isEqualTo(tenantDTO.getLicensing());
    assertThat(converted.getSettings()).isEqualTo(tenantDTO.getSettings());
    assertThat(converted.getTheming()).isEqualTo(tenantDTO.getTheming());
    //content comparision is skipped, due to i18n feature, so the structure is different
  }

  @Test
  void toRestrictedTenantDTO_should_convertAttributesProperly() {
    // given
    MultilingualTenantDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity, TenantConverter.DE);

    // then
    assertThat(restrictedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(restrictedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(restrictedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(restrictedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertContentIsProperlyConverted(tenantDTO, restrictedTenantDTO);
    assertThat(restrictedTenantDTO.getSettings()).isEqualTo(tenantDTO.getSettings());
  }

  @Test
  void toRestrictedTenantDTO_should_convertDefaultValuesForSettingsInCaseOfNull() {
    // given
    MultilingualTenantDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity, TenantConverter.DE);

    // then
    assertThat(restrictedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(restrictedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(restrictedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(restrictedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertContentIsProperlyConverted(tenantDTO, restrictedTenantDTO);
    assertThat(restrictedTenantDTO.getSettings()).isEqualTo(new Settings());
  }

  private static void assertContentIsProperlyConverted(MultilingualTenantDTO tenantDTO, RestrictedTenantDTO restrictedTenantDTO) {
    assertThat(restrictedTenantDTO.getContent().getClaim()).isEqualTo(getGermanTranslation(tenantDTO.getContent().getClaim()));
    assertThat(restrictedTenantDTO.getContent().getPrivacy()).isEqualTo(getGermanTranslation(tenantDTO.getContent().getPrivacy()));
    assertThat(restrictedTenantDTO.getContent().getTermsAndConditions()).isEqualTo(getGermanTranslation(tenantDTO.getContent().getTermsAndConditions()));
    assertThat(restrictedTenantDTO.getContent().getImpressum()).isEqualTo(getGermanTranslation(tenantDTO.getContent().getImpressum()));
  }

  private static String getGermanTranslation(List<Translation> translations) {
    return translations.stream().filter(t->t.getLang().equals("de")).findFirst().get().getValue();
  }

  private static String getGermanTranslation(Map<String, String> translations) {
    return translations.get("de");
  }

  @Test
  void toBasicLicensingTenantDTO_should_convertAttributesProperly() {
    // given
    MultilingualTenantDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    BasicTenantLicensingDTO basicTenantLicensingDTO = tenantConverter.toBasicLicensingTenantDTO(
        entity);

    // then
    assertThat(basicTenantLicensingDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(basicTenantLicensingDTO.getCreateDate()).isEqualTo(tenantDTO.getCreateDate());
    assertThat(basicTenantLicensingDTO.getUpdateDate()).isEqualTo(tenantDTO.getUpdateDate());
    assertThat(basicTenantLicensingDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(basicTenantLicensingDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(basicTenantLicensingDTO.getLicensing()).isEqualTo(tenantDTO.getLicensing());
  }
}
