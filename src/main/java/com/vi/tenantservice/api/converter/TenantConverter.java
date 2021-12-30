package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TenantConverter {
    public TenantEntity toEntity(TenantDTO tenantDTO) {
        TenantEntity.TenantEntityBuilder builder = TenantEntity
                .builder()
                .id(tenantDTO.getId())
                .name(tenantDTO.getName())
                .subdomain(tenantDTO.getSubdomain());
        contentToEntity(tenantDTO, builder);
        licensingToEntity(tenantDTO, builder);
        themingToEntity(tenantDTO, builder);
        return builder.build();
    }

    public TenantEntity toEntity(TenantEntity targetEntity, TenantDTO tenantDTO) {
        TenantEntity sourceEntity = toEntity(tenantDTO);
        BeanUtils.copyProperties(sourceEntity, targetEntity, "id", "createDate", "updateDate");
        return targetEntity;
    }

    private void contentToEntity(TenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getContent() != null) {
            builder
                    .contentClaim(tenantDTO.getContent().getClaim())
                    .contentImpressum(tenantDTO.getContent().getImpressum());
        }
    }

    private void licensingToEntity(TenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getLicensing() != null) {
            builder
                    .licensingAllowedNumberOfUsers(tenantDTO.getLicensing().getAllowedNumberOfUsers());
        }
    }

    private void themingToEntity(TenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getTheming() != null) {
            builder
                    .themingFavicon(tenantDTO.getTheming().getFavicon())
                    .themingLogo(tenantDTO.getTheming().getLogo())
                    .themingPrimaryColor(tenantDTO.getTheming().getPrimaryColor())
                    .themingSecondaryColor(tenantDTO.getTheming().getSecondaryColor());
        }
    }

    public TenantDTO toDTO(TenantEntity tenant) {
        TenantDTO dto = new TenantDTO();
        dto.setId(tenant.getId());
        dto.setName(tenant.getName());
        dto.setSubdomain(tenant.getSubdomain());
        dto.setContent(toContentDTO(tenant));
        dto.setTheming(toThemingDTO(tenant));
        dto.setLicensing(toLicensingDTO(tenant));
        if (tenant.getCreateDate() != null) {
            dto.setCreateDate(tenant.getCreateDate().toString());
        }
        if (tenant.getUpdateDate() != null) {
            dto.setUpdateDate(tenant.getUpdateDate().toString());
        }
        return dto;
    }

    private Licensing toLicensingDTO(TenantEntity tenant) {
        Licensing licensing = new Licensing();
        licensing.setAllowedNumberOfUsers(tenant.getLicensingAllowedNumberOfUsers());
        return licensing;
    }

    private Theming toThemingDTO(TenantEntity tenant) {
        Theming theming = new Theming();
        theming.setFavicon(tenant.getThemingFavicon());
        theming.setLogo(tenant.getThemingLogo());
        theming.setPrimaryColor(tenant.getThemingPrimaryColor());
        theming.setSecondaryColor(tenant.getThemingSecondaryColor());
        return theming;
    }

    private Content toContentDTO(TenantEntity tenant) {
        Content content = new Content();
        content.setClaim(tenant.getContentClaim());
        content.setImpressum(tenant.getContentImpressum());
        return content;
    }
}
