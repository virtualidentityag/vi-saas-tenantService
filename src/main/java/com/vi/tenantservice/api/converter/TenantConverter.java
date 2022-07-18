package com.vi.tenantservice.api.converter;

import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;

import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.Licensing;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantEntity.TenantEntityBuilder;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.api.util.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantConverter {

  public TenantEntity toEntity(TenantDTO tenantDTO) {
    var builder = TenantEntity
        .builder()
        .id(tenantDTO.getId())
        .name(tenantDTO.getName())
        .subdomain(tenantDTO.getSubdomain());
    contentToEntity(tenantDTO, builder);
    licensingToEntity(tenantDTO, builder);
    themingToEntity(tenantDTO, builder);
    settingsToEntity(tenantDTO, builder);
    return builder.build();
  }

  private void settingsToEntity(TenantDTO tenantDTO, TenantEntityBuilder builder) {
    if (tenantDTO.getSettings() != null) {
      TenantSettings tenantSettings = toEntitySettings(tenantDTO.getSettings());
      builder.settings(convertToJson(tenantSettings)).build();
    }
  }

  private TenantSettings toEntitySettings(Settings settings) {
    return TenantSettings.builder()
        .topicsInRegistrationEnabled(nullAsFalse(settings.getTopicsInRegistrationEnabled()))
        .featureDemographicsEnabled(nullAsFalse(settings.getFeatureDemographicsEnabled()))
        .featureTopicsEnabled(nullAsFalse(settings.getFeatureTopicsEnabled()))
        .featureAppointmentsEnabled(nullAsFalse(settings.getFeatureAppointmentsEnabled()))
        .featureMultitenancyEnabled(nullAsFalse(settings.getFeatureMultitenancyEnabled()))
        .featureStatisticsEnabled(nullAsFalse(settings.getFeatureStatisticsEnabled()))
        .build();
  }

  private boolean nullAsFalse(Boolean topicsInRegistrationEnabled) {
    return Boolean.TRUE.equals(topicsInRegistrationEnabled);
  }

  public TenantEntity toEntity(TenantEntity targetEntity, TenantDTO tenantDTO) {
    var sourceEntity = toEntity(tenantDTO);
    BeanUtils.copyProperties(sourceEntity, targetEntity, "id", "createDate", "updateDate");
    return targetEntity;
  }

  private void contentToEntity(TenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
    if (tenantDTO.getContent() != null) {
      builder
          .contentClaim(tenantDTO.getContent().getClaim())
          .contentImpressum(tenantDTO.getContent().getImpressum())
          .contentPrivacy(tenantDTO.getContent().getPrivacy())
          .contentTermsAndConditions(tenantDTO.getContent().getTermsAndConditions());
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
    var tenantDTO = new TenantDTO()
        .id(tenant.getId())
        .name(tenant.getName())
        .subdomain(tenant.getSubdomain())
        .content(toContentDTO(tenant))
        .theming(toThemingDTO(tenant))
        .licensing(toLicensingDTO(tenant))
        .settings(getSettings(tenant));
    if (tenant.getCreateDate() != null) {
      tenantDTO.setCreateDate(tenant.getCreateDate().toString());
    }
    if (tenant.getUpdateDate() != null) {
      tenantDTO.setUpdateDate(tenant.getUpdateDate().toString());
    }
    return tenantDTO;
  }

  private Settings getSettings(TenantEntity tenant) {
    if (tenant.getSettings() == null) {
      return new Settings();
    } else {
      return getSettingsIfNotNull(tenant.getSettings());
    }
  }

  private Settings getSettingsIfNotNull(String settingsJson) {
    TenantSettings tenantSettings = JsonConverter.convertFromJson(settingsJson);
    return new Settings()
        .topicsInRegistrationEnabled(tenantSettings.isTopicsInRegistrationEnabled())
        .featureDemographicsEnabled(tenantSettings.isFeatureDemographicsEnabled())
        .featureTopicsEnabled(tenantSettings.isFeatureTopicsEnabled());
  }

  public RestrictedTenantDTO toRestrictedTenantDTO(TenantEntity tenant) {
    return new RestrictedTenantDTO()
        .id(tenant.getId())
        .name(tenant.getName())
        .content(toContentDTO(tenant))
        .theming(toThemingDTO(tenant))
        .subdomain(tenant.getSubdomain())
        .settings(getSettings(tenant));
  }

  public BasicTenantLicensingDTO toBasicLicensingTenantDTO(TenantEntity tenant) {
    var basicTenantLicensingDTO = new BasicTenantLicensingDTO()
        .id(tenant.getId())
        .name(tenant.getName())
        .subdomain(tenant.getSubdomain())
        .licensing(toLicensingDTO(tenant));

    if (tenant.getCreateDate() != null) {
      basicTenantLicensingDTO.setCreateDate(tenant.getCreateDate().toString());
    }
    if (tenant.getUpdateDate() != null) {
      basicTenantLicensingDTO.setUpdateDate(tenant.getUpdateDate().toString());
    }
    return basicTenantLicensingDTO;
  }


  public Licensing toLicensingDTO(TenantEntity tenant) {
    return new Licensing()
        .allowedNumberOfUsers(tenant.getLicensingAllowedNumberOfUsers());
  }

  private Theming toThemingDTO(TenantEntity tenant) {
    return new Theming()
        .favicon(tenant.getThemingFavicon())
        .logo(tenant.getThemingLogo())
        .primaryColor(tenant.getThemingPrimaryColor())
        .secondaryColor(tenant.getThemingSecondaryColor());
  }

  private Content toContentDTO(TenantEntity tenant) {
    return new Content()
        .claim(tenant.getContentClaim())
        .impressum(tenant.getContentImpressum())
        .privacy(tenant.getContentPrivacy())
        .termsAndConditions(tenant.getContentTermsAndConditions());
  }
}
