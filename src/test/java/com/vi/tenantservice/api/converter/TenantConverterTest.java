package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.util.TenantTestDataBuilder;
import org.junit.jupiter.api.Test;

class TenantConverterTest {

  TenantConverter tenantConverter = new TenantConverter();

  @Test
  void toEntity_should_convertToEntityAndBackToDTO() {
    // given
    TenantDTO tenantDTO = new TenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();

    // when
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // then
    assertThat(tenantConverter.toDTO(entity)).isEqualTo(tenantDTO);
  }

  @Test
  void toRestrictedTenantDTO_should_convertAttributesProperly() {
    // given
    TenantDTO tenantDTO = new TenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity);

    // then
    assertThat(restrictedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(restrictedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(restrictedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(restrictedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertThat(restrictedTenantDTO.getContent()).isEqualTo(tenantDTO.getContent());
    assertThat(restrictedTenantDTO.getSettings()).isEqualTo(tenantDTO.getSettings());
  }

  @Test
  void toRestrictedTenantDTO_should_convertDefaultValuesForSettingsInCaseOfNull() {
    // given
    TenantDTO tenantDTO = new TenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(entity);

    // then
    assertThat(restrictedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(restrictedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(restrictedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(restrictedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertThat(restrictedTenantDTO.getContent()).isEqualTo(tenantDTO.getContent());
    assertThat(restrictedTenantDTO.getSettings()).isEqualTo(new Settings());
  }

  @Test
  void toBasicLicensingTenantDTO_should_convertAttributesProperly() {
    // given
    TenantDTO tenantDTO = new TenantTestDataBuilder().tenantDTO()
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
