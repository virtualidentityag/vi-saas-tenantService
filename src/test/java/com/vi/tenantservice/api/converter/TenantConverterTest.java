package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.*;
import org.junit.jupiter.api.Test;

class TenantConverterTest {

    TenantConverter tenantConverter = new TenantConverter();

    @Test
    void shouldConvertToEntityAndBackToDTO() {
        // given
        TenantDTO tenantDTO = new TenantConverterTestDataBuilder().tenantDTO()
                .withContent().withTheming().withLicensing().build();

        // when
        TenantEntity entity = tenantConverter.toEntity(tenantDTO);

        // then
        assertThat(tenantConverter.toDTO(entity)).isEqualTo(tenantDTO);
    }

    private class TenantConverterTestDataBuilder {

        private static final String SUBDOMAIN = "subdomain";
        private static final String NAME = "name";
        private static final long ID = 1L;
        private static final String IMPRESSUM = "Impressum";
        private static final String CLAIM = "claim";
        private static final String SECONDARY_COLOR = "secondaryColor";
        private static final String PRIMARY_COLOR = "primary color";
        private static final String FAVICON = "favicon";
        private static final String LOGO = "logo";
        private static final int ALLOWED_NUMBER_OF_USERS = 2000;

        TenantDTO tenantDTO = new TenantDTO();

        public TenantConverterTestDataBuilder tenantDTO() {
            tenantDTO = new TenantDTO();
            tenantDTO.setId(ID);
            tenantDTO.setName(NAME);
            tenantDTO.setSubdomain(SUBDOMAIN);
            return this;
        }

        public TenantConverterTestDataBuilder withContent() {
            tenantDTO.setContent(content());
            return this;
        }

        public TenantConverterTestDataBuilder withTheming() {
            tenantDTO.setTheming(theming());
            return this;
        }

        public TenantConverterTestDataBuilder withLicensing() {
            tenantDTO.setLicensing(licensing());
            return this;
        }

        private Licensing licensing() {
            Licensing licensing = new Licensing();
            licensing.setAllowedNumberOfUsers(ALLOWED_NUMBER_OF_USERS);
            return licensing;
        }


        public TenantDTO build() {
            return tenantDTO;
        }

        private Theming theming() {
            Theming theming = new Theming();
            theming.setSecondaryColor(SECONDARY_COLOR);
            theming.setPrimaryColor(PRIMARY_COLOR);
            theming.setFavicon(FAVICON);
            theming.setLogo(LOGO);
            return theming;
        }

        private Content content() {
            Content content = new Content();
            content.setImpressum(IMPRESSUM);
            content.setClaim(CLAIM);
            return content;
        }
    }

}