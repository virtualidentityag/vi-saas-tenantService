package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.LegalTenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.util.LegalTenantTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LegalTenantConverterTest {

    LegalTenantConverter legalTenantConverter = new LegalTenantConverter();

    @Test
    void toLegalTenantDTO_should_convertAttributesProperly() {
        // given
        LegalTenantDTO tenantDTO = new LegalTenantTestDataBuilder().tenantDTO().withContent().build();

        TenantEntity entity = legalTenantConverter.toEntity(tenantDTO);

        // when
        var result = legalTenantConverter.toLegalTenantDTO(entity);

        // then
        assertThat(result.getId()).isEqualTo(tenantDTO.getId());
        assertThat(result.getContent().getImpressum()).isEqualTo(tenantDTO.getContent().getImpressum());
        assertThat(result.getContent().getPrivacy()).isEqualTo(tenantDTO.getContent().getPrivacy());
        assertThat(result.getContent().getTermsAndConditions()).isEqualTo(tenantDTO.getContent().getTermsAndConditions());
    }

}