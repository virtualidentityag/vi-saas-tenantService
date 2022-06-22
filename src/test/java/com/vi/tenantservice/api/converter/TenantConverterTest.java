package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.LimitedTenantDTO;
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
  void toLimitedTenantDTO_should_convertAttributesProperly() {
    // given
    TenantDTO tenantDTO = new TenantTestDataBuilder().tenantDTO()
        .withContent().withTheming().withLicensing().withSettings().build();
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // when
    LimitedTenantDTO limitedTenantDTO = tenantConverter.toLimitedTenantDTO(entity);

    // then
    assertThat(limitedTenantDTO.getName()).isEqualTo(tenantDTO.getName());
    assertThat(limitedTenantDTO.getId()).isEqualTo(tenantDTO.getId());
    assertThat(limitedTenantDTO.getSubdomain()).isEqualTo(tenantDTO.getSubdomain());
    assertThat(limitedTenantDTO.getTheming()).isEqualTo(tenantDTO.getTheming());
    assertThat(limitedTenantDTO.getContent()).isEqualTo(tenantDTO.getContent());
    assertThat(limitedTenantDTO.getSettings()).isEqualTo(tenantDTO.getSettings());
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
