package com.vi.tenantservice.api.facade;


import com.google.common.collect.Lists;
import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.service.TranslationService;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import com.vi.tenantservice.config.security.AuthorisationService;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Facade to encapsulate services and logic needed to manage tenants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceFacade {

    private final @NonNull TenantService tenantService;
    private final @NonNull TenantConverter tenantConverter;
    private final @NonNull TenantInputSanitizer tenantInputSanitizer;
    private final @NonNull TenantFacadeAuthorisationService tenantFacadeAuthorisationService;
    private final @NonNull AuthorisationService authorisationService;

    private final @NonNull TranslationService translationService;

    @Value("${feature.multitenancy.with.single.domain.enabled}")
    private boolean multitenancyWithSingleDomain;

    public MultilingualTenantDTO createTenant(MultilingualTenantDTO tenantDTO) {
        log.info("Creating new tenant");
        MultilingualTenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
        validateTenantInput(tenantDTO);
        var entity = tenantConverter.toEntity(sanitizedTenantDTO);
        setContentActivationDates(entity, tenantDTO);
        return tenantConverter.toMultilingualDTO(tenantService.create(entity));
    }

    public MultilingualTenantDTO updateTenant(Long id, MultilingualTenantDTO tenantDTO) {
        tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
        validateTenantInput(tenantDTO);
        MultilingualTenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
        log.info("Attempting to update tenant with id {}", id);
        return updateWithSanitizedInput(id, sanitizedTenantDTO);
    }

    private void validateTenantInput(MultilingualTenantDTO tenantDTO) {
        var isoCountries = Arrays.stream(Locale.getISOCountries()).collect(Collectors.toList());
        validateContent(tenantDTO, isoCountries);
    }

    private void validateContent(MultilingualTenantDTO tenantDTO, List<String> isoCountries) {
        if (tenantDTO.getContent() != null) {
            validateTranslationKeys(isoCountries, getLanguageUppercaseKeys(tenantDTO.getContent().getImpressum()));
            validateTranslationKeys(isoCountries, getLanguageUppercaseKeys(tenantDTO.getContent().getPrivacy()));
            validateTranslationKeys(isoCountries, getLanguageUppercaseKeys(tenantDTO.getContent().getTermsAndConditions()));
            validateTranslationKeys(isoCountries, getLanguageUppercaseKeys(tenantDTO.getContent().getClaim()));
        }
    }

    private void validateTranslationKeys(List<String> isoCountries, List<String> keys) {
        if (!keys.isEmpty() && !isoCountries.containsAll(keys)) {
            throw new TenantValidationException(HttpStatusExceptionReason.LANGUAGE_KEY_NOT_VALID);
        }
    }

    private static List<String> getLanguageUppercaseKeys(Map<String, String> translatedMap) {
        if (translatedMap == null) {
           return Lists.newArrayList();
        }
        return translatedMap.keySet().stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
    }

    private MultilingualTenantDTO updateWithSanitizedInput(Long id, MultilingualTenantDTO sanitizedTenantDTO) {
        var tenantById = tenantService.findTenantById(id);
        if (tenantById.isPresent()) {
            return updateExistingTenant(sanitizedTenantDTO, tenantById.get());
        } else {
            throw new TenantNotFoundException("Tenant with given id could not be found : " + id);
        }
    }

    private MultilingualTenantDTO updateExistingTenant(MultilingualTenantDTO sanitizedTenantDTO,
                                                       TenantEntity existingTenant) {
        tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(sanitizedTenantDTO, existingTenant);
        var updatedEntity = tenantConverter.toEntity(existingTenant, sanitizedTenantDTO);
        log.info("Tenant with id {} updated", existingTenant.getId());
        setContentActivationDates(updatedEntity, sanitizedTenantDTO);
        updatedEntity = tenantService.update(updatedEntity);
        return tenantConverter.toMultilingualDTO(updatedEntity);
    }

    private void setContentActivationDates(TenantEntity entity,
        MultilingualTenantDTO tenantDTO) {
      MultilingualContent content = tenantDTO.getContent();

      if(content == null) {
          return;
      }

      if (content.getConfirmPrivacy() != null && content.getConfirmPrivacy()) {
        entity.setContentPrivacyActivationDate(LocalDateTime.now());
      }

      if (content.getConfirmTermsAndConditions() != null && content.getConfirmTermsAndConditions()) {
        entity.setContentTermsAndConditionsActivationDate(LocalDateTime.now());
      }

    }

    public Optional<TenantDTO> findTenantById(Long id) {
        tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
        var tenantById = tenantService.findTenantById(id);
        return tenantById.isEmpty() ? Optional.empty()
                : Optional.of(tenantConverter.toDTO(tenantById.get(), translationService.getCurrentLanguageContext()));
    }

    public Optional<MultilingualTenantDTO> findMultilingualTenantById(Long id) {
        tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
        var tenantById = tenantService.findTenantById(id);
        return tenantById.isEmpty() ? Optional.empty()
                : Optional.of(tenantConverter.toMultilingualDTO(tenantById.get()));
    }

    public Optional<RestrictedTenantDTO> findRestrictedTenantById(Long id) {
        var tenantById = tenantService.findTenantById(id);

        String lang = translationService.getCurrentLanguageContext();
        return tenantById.isEmpty() ? Optional.empty()
                : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantById.get(), lang));
    }

    public List<BasicTenantLicensingDTO> getAllTenants() {
        var tenantEntities = tenantService.getAllTenants();
        return tenantEntities.stream().map(tenantConverter::toBasicLicensingTenantDTO).collect(
                Collectors.toList());
    }

    public Optional<RestrictedTenantDTO> findTenantBySubdomain(String subdomain, Long optionalTenantIdOverride) {
        var tenantBySubdomain = tenantService.findTenantBySubdomain(subdomain);

        Optional<Long> tenantIdFromRequestOrCookie = authorisationService.resolveTenantFromRequest(optionalTenantIdOverride);
        if (multitenancyWithSingleDomain && tenantIdFromRequestOrCookie.isPresent()) {
            return getSingleDomainSpecificTenantData(tenantBySubdomain, tenantIdFromRequestOrCookie.get());
        }

        String lang = translationService.getCurrentLanguageContext();
        return tenantBySubdomain.isEmpty() ? Optional.empty()
                : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantBySubdomain.get(), lang));
    }

    public Optional<RestrictedTenantDTO> getSingleDomainSpecificTenantData(
            Optional<TenantEntity> mainTenantForSingleDomainMultitenancy, Long resolvedTenantId) {

        Optional<TenantEntity> resolvedTenant = tenantService.findTenantById(resolvedTenantId);
        if (resolvedTenant.isEmpty()) {
            throw new BadRequestException("Tenant not found for id " + resolvedTenantId);
        }
        String lang = translationService.getCurrentLanguageContext();
        RestrictedTenantDTO restrictedTenantDTO = tenantConverter.toRestrictedTenantDTO(mainTenantForSingleDomainMultitenancy.get(), lang);
        restrictedTenantDTO.getContent().setPrivacy(resolvedTenant.get().getContentPrivacy());
        return Optional.of(restrictedTenantDTO);
    }

    public Optional<RestrictedTenantDTO> getSingleTenant() {
        var tenantEntities = tenantService.getAllTenants();
        if (tenantEntities != null && tenantEntities.size() == 1) {
            var tenantEntity = tenantEntities.get(0);
            String lang = translationService.getCurrentLanguageContext();
            return Optional.of(tenantConverter.toRestrictedTenantDTO(tenantEntity, lang));
        } else {
            throw new IllegalStateException("Not exactly one tenant was found.");
        }
    }
}
