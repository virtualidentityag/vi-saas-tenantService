package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

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
        .withContent().withTheming().withLicensing().build();

    // when
    TenantEntity entity = tenantConverter.toEntity(tenantDTO);

    // then
    assertThat(tenantConverter.toDTO(entity)).isEqualTo(tenantDTO);
  }
}
