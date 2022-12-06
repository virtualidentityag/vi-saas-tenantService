package com.vi.tenantservice.api.converter;

import com.google.common.collect.Lists;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.Content;
import com.vi.tenantservice.api.model.Licensing;
import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantEntity.TenantEntityBuilder;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.model.Theming;
import com.vi.tenantservice.api.model.Translation;
import com.vi.tenantservice.api.util.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.vi.tenantservice.api.util.JsonConverter.convertListFromJson;
import static com.vi.tenantservice.api.util.JsonConverter.convertToJson;

@Component
@Slf4j
public class TenantConverter {

    public static final String DE = "de";

    public TenantEntity toEntity(MultilingualTenantDTO tenantDTO) {
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

    private List<String> nullAsGerman(List<String> activeLanguages) {
        if (activeLanguages == null) {
            return Lists.newArrayList(DE);
        }
        return activeLanguages;
    }

    private boolean nullAsFalse(Boolean topicsInRegistrationEnabled) {
        return Boolean.TRUE.equals(topicsInRegistrationEnabled);
    }

    public TenantEntity toEntity(TenantEntity targetEntity, MultilingualTenantDTO tenantDTO) {
        var sourceEntity = toEntity(tenantDTO);
        BeanUtils.copyProperties(sourceEntity, targetEntity, "id", "createDate", "updateDate");
        return targetEntity;
    }

    private void contentToEntity(MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getContent() != null) {
            builder
                    .contentClaim(convertToJson(tenantDTO.getContent().getClaim()))
                    .contentImpressum(convertToJson(tenantDTO.getContent().getImpressum()))
                    .contentPrivacy(convertToJson(tenantDTO.getContent().getPrivacy()))
                    .contentTermsAndConditions(convertToJson(tenantDTO.getContent().getTermsAndConditions()));
        }
    }

    private void licensingToEntity(MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getLicensing() != null) {
            builder
                    .licensingAllowedNumberOfUsers(tenantDTO.getLicensing().getAllowedNumberOfUsers());
        }
    }

    private void themingToEntity(MultilingualTenantDTO tenantDTO, TenantEntity.TenantEntityBuilder builder) {
        if (tenantDTO.getTheming() != null) {
            builder
                    .themingFavicon(tenantDTO.getTheming().getFavicon())
                    .themingLogo(tenantDTO.getTheming().getLogo())
                    .themingPrimaryColor(tenantDTO.getTheming().getPrimaryColor())
                    .themingSecondaryColor(tenantDTO.getTheming().getSecondaryColor());
        }
    }

    public MultilingualTenantDTO toMultilingualDTO(TenantEntity tenant) {
        var tenantDTO = new MultilingualTenantDTO()
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
        var tenantDTO = new TenantDTO()
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

    private Content toContentDTO(TenantEntity tenant, String lang) {
        return new Content()
                .claim(getTranslatedString(tenant.getContentClaim(), lang))
                .impressum(getTranslatedString(tenant.getContentImpressum(), lang))
                .privacy(getTranslatedString(tenant.getContentPrivacy(), lang))
                .termsAndConditions(getTranslatedString(tenant.getContentTermsAndConditions(), lang));
    }

    private static String getTranslatedString(String jsonValue, String lang) {
        List<Translation> translations = convertListFromJson(jsonValue);
        Optional<Translation> translated = getTranslationForLanguage(lang, translations);
        if (translated.isEmpty()) {
            Optional<Translation> defaultTranslation = getTranslationForLanguage(DE, translations);
            if (defaultTranslation.isEmpty()) {
                log.warn("Default translation for value not available");
                return "";
            } else {
                return defaultTranslation.get().getValue();
            }
        } else {
            return translated.get().getValue();
        }
    }

    private static Optional<Translation> getTranslationForLanguage(String lang, List<Translation> translations) {
        if (lang == null) {
            return Optional.empty();
        }
        return translations.stream().filter(translation -> lang.equals(translation.getLang())).findFirst();
    }

    private MultilingualContent toMultilingualContentDTO(TenantEntity tenant) {
        return new MultilingualContent()
                .claim(convertListFromJson(tenant.getContentClaim()))
                .impressum(convertListFromJson(tenant.getContentImpressum()))
                .privacy(convertListFromJson(tenant.getContentPrivacy()))
                .termsAndConditions(convertListFromJson(tenant.getContentTermsAndConditions()));
    }
}
