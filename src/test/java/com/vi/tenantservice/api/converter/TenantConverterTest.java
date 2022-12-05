package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantMultilingualDTO;
import com.vi.tenantservice.api.util.JsonConverter;
import com.vi.tenantservice.api.util.MultilingualTenantTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantConverterTest {

  TenantConverter tenantConverter = new TenantConverter();

  @Test
  @Disabled("TODO tkuzynow implement")
  void toEntity_should_convertToEntityAndBackToDTO() {
    // given
    TenantMultilingualDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();

    // when
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // then
    TenantDTO converted = tenantConverter.toDTO(entity);
    Assertions.assertThat(converted).usingComparatorForFields((x, y) -> 0, "content").isEqualTo(tenantDTO);

  }

  @Test
  void toRestrictedTenantDTO_should_convertAttributesProperly() {
    // given
    TenantMultilingualDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity);

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
    TenantMultilingualDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity);

    // then
    assertThat(restrictedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(restrictedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(restrictedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(restrictedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertContentIsProperlyConverted(tenantDTO, restrictedTenantDTO);
    assertThat(restrictedTenantDTO.getSettings()).isEqualTo(new Settings());
  }

  private static void assertContentIsProperlyConverted(TenantMultilingualDTO tenantDTO, RestrictedTenantDTO restrictedTenantDTO) {
    assertThat(restrictedTenantDTO.getContent().getClaim()).isEqualTo(JsonConverter.convertToJson(tenantDTO.getContent().getClaim()));
    assertThat(restrictedTenantDTO.getContent().getPrivacy()).isEqualTo(JsonConverter.convertToJson(tenantDTO.getContent().getPrivacy()));
    assertThat(restrictedTenantDTO.getContent().getTermsAndConditions()).isEqualTo(JsonConverter.convertToJson(tenantDTO.getContent().getTermsAndConditions()));
    assertThat(restrictedTenantDTO.getContent().getImpressum()).isEqualTo(JsonConverter.convertToJson(tenantDTO.getContent().getImpressum()));
  }

  @Test
  void toBasicLicensingTenantDTO_should_convertAttributesProperly() {
    // given
    TenantMultilingualDTO tenantDTO = new MultilingualTenantTestDataBuilder().tenantDTO()
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
