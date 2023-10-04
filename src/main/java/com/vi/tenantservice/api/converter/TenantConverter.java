package com.vi.tenantservice.api.converter;

import static com.vi.tenantservice.api.converter.ConverterUtils.nullAsFalse;
import static com.vi.tenantservice.api.converter.ConverterUtils.nullAsGerman;
import static com.vi.tenantservice.api.util.JsonConverter.convertMapFromJson;
import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;
import static com.vi.tenantservice.api.util.JsonConverter.deserializeMapFromJsonString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.vi.tenantservice.api.model.AdminTenantDTO;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.Licensing;
import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.PlaceholderDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantEntity.TenantEntityBuilder;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.api.util.JsonConverter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantConverter {

  public static final String DE = "de";

  public TenantEntity toEntity(MultilingualTenantDTO tenantDTO) {
    var builder =
        TenantEntity.builder()
            .id(tenantDTO.getId())
            .name(tenantDTO.getName())
            .subdomain(tenantDTO.getSubdomain());
    contentToEntity(tenantDTO, builder);
    licensingToEntity(tenantDTO, builder);
    themingToEntity(tenantDTO, builder);
    settingsToEntity(tenantDTO, builder);
    return builder.build();
  }

  private void settingsToEntity(MultilingualTenantDTO tenantDTO, TenantEntityBuilder builder) {
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
        .featureStatisticsEnabled(nullAsFalse(settings.getFeatureStatisticsEnabled()))
        .featureGroupChatV2Enabled(nullAsFalse(settings.getFeatureGroupChatV2Enabled()))
        .featureToolsEnabled(nullAsFalse(settings.getFeatureToolsEnabled()))
        .featureToolsOIDCToken(settings.getFeatureToolsOICDToken())
        .featureAttachmentUploadDisabled(nullAsFalse(settings.getFeatureAttachmentUploadDisabled()))
        .activeLanguages(nullAsGerman(settings.getActiveLanguages()))
        .build();
  }

  public TenantEntity toEntity(TenantEntity targetEntity, MultilingualTenantDTO tenantDTO) {
    var sourceEntity = toEntity(tenantDTO);
    BeanUtils.copyProperties(
        sourceEntity,
        targetEntity,
        "id",
        "createDate",
        "updateDate",
        "contentPrivacyActivationDate",
        "contentTermsAndConditionsActivationDate");
    return targetEntity;
  }

  private void contentToEntity(
      MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
    if (tenantDTO.getContent() != null) {
      builder
          .contentClaim(convertToJson(tenantDTO.getContent().getClaim()))
          .contentImpressum(convertToJson(tenantDTO.getContent().getImpressum()))
          .contentPrivacy(convertToJson(tenantDTO.getContent().getPrivacy()))
          .contentTermsAndConditions(convertToJson(tenantDTO.getContent().getTermsAndConditions()))
          .contentPlaceholders(convertToJson(tenantDTO.getContent().getPlaceholders()));
    }
  }

  private void licensingToEntity(
      MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
    if (tenantDTO.getLicensing() != null) {
      builder.licensingAllowedNumberOfUsers(tenantDTO.getLicensing().getAllowedNumberOfUsers());
    }
  }

  private void themingToEntity(
      MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
    if (tenantDTO.getTheming() != null) {
      builder
          .themingFavicon(tenantDTO.getTheming().getFavicon())
          .themingLogo(tenantDTO.getTheming().getLogo())
          .themingPrimaryColor(tenantDTO.getTheming().getPrimaryColor())
          .themingSecondaryColor(tenantDTO.getTheming().getSecondaryColor());
    }
  }

  public MultilingualTenantDTO toMultilingualDTO(TenantEntity tenant) {
    var tenantDTO =
        new MultilingualTenantDTO()
            .id(tenant.getId())
            .name(tenant.getName())
            .subdomain(tenant.getSubdomain())
            .content(toMultilingualContentDTO(tenant))
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

  public TenantDTO toDTO(TenantEntity tenant, String lang) {
    var tenantDTO =
        new TenantDTO()
            .id(tenant.getId())
            .name(tenant.getName())
            .subdomain(tenant.getSubdomain())
            .content(toContentDTO(tenant, lang))
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
        .featureTopicsEnabled(tenantSettings.isFeatureTopicsEnabled())
        .featureAppointmentsEnabled(tenantSettings.isFeatureAppointmentsEnabled())
        .featureStatisticsEnabled(tenantSettings.isFeatureStatisticsEnabled())
        .featureGroupChatV2Enabled(tenantSettings.isFeatureGroupChatV2Enabled())
        .featureToolsOICDToken(tenantSettings.getFeatureToolsOIDCToken())
        .featureToolsEnabled(tenantSettings.isFeatureToolsEnabled())
        .featureAttachmentUploadDisabled(tenantSettings.isFeatureAttachmentUploadDisabled())
        .activeLanguages(nullAsGerman(tenantSettings.getActiveLanguages()));
  }

  public RestrictedTenantDTO toRestrictedTenantDTO(TenantEntity tenant, String lang) {
    return new RestrictedTenantDTO()
        .id(tenant.getId())
        .name(tenant.getName())
        .content(toContentDTO(tenant, lang))
        .theming(toThemingDTO(tenant))
        .subdomain(tenant.getSubdomain())
        .settings(getSettings(tenant));
  }

  public BasicTenantLicensingDTO toBasicLicensingTenantDTO(TenantEntity tenant) {
    var basicTenantLicensingDTO =
        new BasicTenantLicensingDTO()
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
    return new Licensing().allowedNumberOfUsers(tenant.getLicensingAllowedNumberOfUsers());
  }

  private Theming toThemingDTO(TenantEntity tenant) {
    return new Theming()
        .favicon(tenant.getThemingFavicon())
        .logo(tenant.getThemingLogo())
        .primaryColor(tenant.getThemingPrimaryColor())
        .secondaryColor(tenant.getThemingSecondaryColor());
  }

  private Content toContentDTO(TenantEntity tenant, String lang) {
    return new Content()
        .claim(getTranslatedStringFromMap(tenant.getContentClaim(), lang))
        .impressum(getTranslatedStringFromMap(tenant.getContentImpressum(), lang))
        .privacy(getTranslatedStringFromMap(tenant.getContentPrivacy(), lang))
        .termsAndConditions(getTranslatedStringFromMap(tenant.getContentTermsAndConditions(), lang))
        .dataPrivacyConfirmation(tenant.getContentPrivacyActivationDate())
        .termsAndConditionsConfirmation(tenant.getContentTermsAndConditionsActivationDate())
        .placeholders(getTranslatedPlaceholders(tenant, lang));
  }

  private static List<PlaceholderDTO> getTranslatedPlaceholders(TenantEntity tenant, String lang) {
    var placeholders = convertPlaceholdersFromJson(tenant.getContentPlaceholders());
    return placeholders.get(lang);
  }

  private static String getTranslatedStringFromMap(String jsonValue, String lang) {
    Map<String, String> translations = convertMapFromJson(jsonValue);
    if (lang == null || !translations.containsKey(lang)) {
      if (translations.containsKey(DE)) {
        return translations.get(DE);
      } else {
        log.warn("Default translation for value not available");
        return "";
      }
    } else {
      return translations.get(lang);
    }
  }

  private MultilingualContent toMultilingualContentDTO(TenantEntity tenant) {
    return new MultilingualContent()
        .claim(convertMapFromJson(tenant.getContentClaim()))
        .impressum(convertMapFromJson(tenant.getContentImpressum()))
        .privacy(convertMapFromJson(tenant.getContentPrivacy()))
        .placeholders(convertPlaceholdersFromJson(tenant.getContentPlaceholders()))
        .termsAndConditions(convertMapFromJson(tenant.getContentTermsAndConditions()));
  }

  private static Map<String, List<PlaceholderDTO>> convertPlaceholdersFromJson(
      String contentPlaceholders) {

    if (contentPlaceholders == null) {
      return Maps.newHashMap();
    }
    var result =
        deserializeMapFromJsonString(
            contentPlaceholders, new TypeReference<Map<String, List<PlaceholderDTO>>>() {});
    if (result == null) {
      log.warn("Could not deserialize map from json.");
      return Maps.newHashMap();
    }
    return result;
  }

  public AdminTenantDTO toAdminTenantDTO(TenantEntity tenant) {
    var adminTenantDTO =
        new AdminTenantDTO(tenant.getId(), tenant.getName(), tenant.getSubdomain())
            .beraterCount(tenant.getLicensingAllowedNumberOfUsers());
    if (tenant.getCreateDate() != null) {
      adminTenantDTO.setCreateDate(tenant.getCreateDate().toString());
    }
    if (tenant.getUpdateDate() != null) {
      adminTenantDTO.setUpdateDate(tenant.getUpdateDate().toString());
    }
    return adminTenantDTO;
  }
}
