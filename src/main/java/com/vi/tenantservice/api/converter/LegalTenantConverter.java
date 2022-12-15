package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.LegalTenantDTO;
import com.vi.tenantservice.api.model.MultilingualLegalContent;
import com.vi.tenantservice.api.model.TenantEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.vi.tenantservice.api.util.JsonConverter.convertMapFromJson;
import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;
@Component
@Slf4j
public class LegalTenantConverter {

    public TenantEntity toEntity(LegalTenantDTO tenantDTO) {
        var builder = TenantEntity
                .builder()
                .id(tenantDTO.getId());
        contentToEntity(tenantDTO, builder);
        return builder.build();
    }

    public TenantEntity toEntity(TenantEntity targetEntity, LegalTenantDTO tenantDTO) {
        var sourceEntity = toEntity(tenantDTO);
        targetEntity.setContentImpressum(sourceEntity.getContentImpressum());
        targetEntity.setContentPrivacy(sourceEntity.getContentPrivacy());
        targetEntity.setContentTermsAndConditions(sourceEntity.getContentTermsAndConditions());
        targetEntity.setContentPrivacyActivationDate(sourceEntity.getContentPrivacyActivationDate());
        targetEntity.setContentTermsAndConditionsActivationDate(sourceEntity.getContentTermsAndConditionsActivationDate());
        return targetEntity;
    }

    private void contentToEntity(LegalTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        var content = tenantDTO.getContent();
        if (content != null) {
            builder
                    .contentImpressum(convertToJson(content.getImpressum()))
                    .contentPrivacy(convertToJson(content.getPrivacy()))
                    .contentTermsAndConditions(convertToJson(content.getTermsAndConditions()));

            if (content.getConfirmPrivacy() != null && content.getConfirmPrivacy()) {
                builder.contentPrivacyActivationDate(LocalDateTime.now());
            }

            if (content.getConfirmTermsAndConditions() != null && content.getConfirmTermsAndConditions()) {
                builder.contentTermsAndConditionsActivationDate(LocalDateTime.now());
            }
        }
    }

    public LegalTenantDTO toLegalTenantDTO(TenantEntity tenant) {
        var tenantDTO = new LegalTenantDTO()
                .id(tenant.getId())
                .content(toLegalContentDTO(tenant));
        if (tenant.getCreateDate() != null) {
            tenantDTO.setCreateDate(tenant.getCreateDate().toString());
        }
        if (tenant.getUpdateDate() != null) {
            tenantDTO.setUpdateDate(tenant.getUpdateDate().toString());
        }
        return tenantDTO;
    }

    private MultilingualLegalContent toLegalContentDTO(TenantEntity tenant) {
        return new MultilingualLegalContent()
                .impressum(convertMapFromJson(tenant.getContentImpressum()))
                .privacy(convertMapFromJson(tenant.getContentPrivacy()))
                .termsAndConditions(convertMapFromJson(tenant.getContentTermsAndConditions()));
    }
}
