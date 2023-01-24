package com.vi.tenantservice.api.facade;

import static java.util.Objects.nonNull;
import static liquibase.repackaged.org.apache.commons.collections4.ListUtils.emptyIfNull;

import com.google.common.collect.Lists;
import com.vi.tenantservice.api.authorisation.Authority.AuthorityValue;
import com.vi.tenantservice.api.converter.TenantConverter;
import com.vi.tenantservice.api.converter.TenantExtendedSettingsConverter;
import com.vi.tenantservice.api.exception.TenantNotFoundException;
import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason;
import com.vi.tenantservice.api.model.AdminTenantDTO;
import com.vi.tenantservice.api.model.BasicTenantLicensingDTO;
import com.vi.tenantservice.api.model.MultilingualContent;
import com.vi.tenantservice.api.model.MultilingualTenantDTO;
import com.vi.tenantservice.api.model.RestrictedTenantDTO;
import com.vi.tenantservice.api.model.Settings;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantEntity.TenantBase;
import com.vi.tenantservice.api.service.TenantService;
import com.vi.tenantservice.api.service.TranslationService;
import com.vi.tenantservice.api.service.consultingtype.ApplicationSettingsService;
import com.vi.tenantservice.api.service.consultingtype.ConsultingTypeService;
import com.vi.tenantservice.api.service.consultingtype.UserAdminService;
import com.vi.tenantservice.api.tenant.SubdomainExtractor;
import com.vi.tenantservice.api.validation.TenantInputSanitizer;
import com.vi.tenantservice.config.security.AuthorisationService;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import com.vi.tenantservice.useradminservice.generated.web.model.AdminResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

/** Facade to encapsulate services and logic needed to manage tenants */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceFacade {

  private static final int TECHNICAL_TENANT_ID = 0;
  private final @NonNull TenantService tenantService;
  private final @NonNull TenantConverter tenantConverter;
  private final @NonNull TenantInputSanitizer tenantInputSanitizer;
  private final @NonNull TenantFacadeAuthorisationService tenantFacadeAuthorisationService;
  private final @NonNull AuthorisationService authorisationService;
  private final @NonNull TranslationService translationService;
  private final @NonNull ConsultingTypeService consultingTypeService;
  private final @NonNull SubdomainExtractor subdomainExtractor;
  private final @NonNull ApplicationSettingsService applicationSettingsService;

  private final @NonNull UserAdminService userAdminService;

  private final @NonNull TenantExtendedSettingsConverter tenantExtendedSettingsConverter;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomain;

  public MultilingualTenantDTO createTenant(MultilingualTenantDTO tenantDTO) {
    log.info("Creating new tenant");
    MultilingualTenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    validateCreateTenantInput(tenantDTO);
    var entity = tenantConverter.toEntity(sanitizedTenantDTO);
    setContentActivationDates(entity, tenantDTO);
    TenantEntity createdTenant = tenantService.create(entity);
    setDefaultSettings(createdTenant);
    return tenantConverter.toMultilingualDTO(createdTenant);
  }

  private void setDefaultSettings(TenantEntity createdTenant) {
    consultingTypeService.createDefaultConsultingTypes(createdTenant.getId());
    if (isAttemptToCreateFirstNonTechnicalTenant(createdTenant.getId())) {
      validateSubDomain(createdTenant.getSubdomain());
      applicationSettingsService.saveMainTenantSubDomain(createdTenant.getSubdomain());
    }
  }

  private void validateSubDomain(String subdomain) {
    Optional<String> subDomainFromUrl = subdomainExtractor.getCurrentSubdomain();
    if (subDomainFromUrl.isPresent() && !subdomain.equals(subDomainFromUrl.get())) {
      throw new TenantValidationException(
          HttpStatusExceptionReason.SUBDOMAIN_IN_REQUEST_BODY_NOT_EQUAL_TO_SUBDOMAIN_IN_URL);
    }
  }

  public MultilingualTenantDTO updateTenant(Long id, MultilingualTenantDTO tenantDTO) {
    tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
    validateTenantInput(tenantDTO);
    MultilingualTenantDTO sanitizedTenantDTO = tenantInputSanitizer.sanitize(tenantDTO);
    log.info("Attempting to update tenant with id {}", id);
    return updateWithSanitizedInput(id, sanitizedTenantDTO);
  }

  private boolean isAttemptToCreateFirstNonTechnicalTenant(Long tenantId) {
    return tenantId != 0L && multitenancyWithSingleDomain && onlyTechnicalTenantExists();
  }

  private boolean onlyTechnicalTenantExists() {
    List<TenantEntity> tenants = tenantService.getAllTenants();
    return tenants.size() == 1 && tenants.get(0).getId().equals(0L);
  }

  private void validateTenantInput(MultilingualTenantDTO tenantDTO) {
    var isoCountries = Arrays.stream(Locale.getISOLanguages()).collect(Collectors.toList());
    validateContent(tenantDTO, isoCountries);
    validateSettings(tenantDTO.getSettings());
  }

  private void validateSettings(Settings settings) {
    if (settings != null && settings.getActiveLanguages() != null) {
      validateEachLanguageHasCorrectFormat(settings.getActiveLanguages());
    }
  }

  private void validateEachLanguageHasCorrectFormat(List<String> activeLanguages) {
    List<String> invalidLanguages =
        activeLanguages.stream()
            .filter(language -> language == null || language.length() != 2)
            .collect(Collectors.toList());
    if (!invalidLanguages.isEmpty()) {
      throw new TenantValidationException(
          HttpStatusExceptionReason.ID_MUST_BE_NULL_WHEN_CREATING_TENANT);
    }
  }

  private void validateCreateTenantInput(MultilingualTenantDTO tenantDTO) {
    validateTenantInput(tenantDTO);
    validateId(tenantDTO);
  }

  private void validateId(MultilingualTenantDTO tenantDTO) {
    if (nonNull(tenantDTO.getId())) {
      throw new TenantValidationException(
          HttpStatusExceptionReason.ID_MUST_BE_NULL_WHEN_CREATING_TENANT);
    }
  }

  private void validateContent(MultilingualTenantDTO tenantDTO, List<String> isoCountries) {
    if (tenantDTO.getContent() != null) {
      validateTranslationKeys(
          isoCountries, getLanguageLowercaseKeys(tenantDTO.getContent().getImpressum()));
      validateTranslationKeys(
          isoCountries, getLanguageLowercaseKeys(tenantDTO.getContent().getPrivacy()));
      validateTranslationKeys(
          isoCountries, getLanguageLowercaseKeys(tenantDTO.getContent().getTermsAndConditions()));
      validateTranslationKeys(
          isoCountries, getLanguageLowercaseKeys(tenantDTO.getContent().getClaim()));
    }
  }

  private void validateTranslationKeys(List<String> isoCountries, List<String> keys) {
    if (!keys.isEmpty() && !isoCountries.containsAll(keys)) {
      throw new TenantValidationException(HttpStatusExceptionReason.LANGUAGE_KEY_NOT_VALID);
    }
  }

  private static List<String> getLanguageLowercaseKeys(Map<String, String> translatedMap) {
    if (translatedMap == null) {
      return Lists.newArrayList();
    }
    return translatedMap.keySet().stream().map(s -> s.toLowerCase()).collect(Collectors.toList());
  }

  private MultilingualTenantDTO updateWithSanitizedInput(
      Long id, MultilingualTenantDTO sanitizedTenantDTO) {
    var tenantById = tenantService.findTenantById(id);
    if (tenantById.isPresent()) {
      return updateExistingTenant(sanitizedTenantDTO, tenantById.get());
    } else {
      throw new TenantNotFoundException("Tenant with given id could not be found : " + id);
    }
  }

  private MultilingualTenantDTO updateExistingTenant(
      MultilingualTenantDTO sanitizedTenantDTO, TenantEntity existingTenant) {
    tenantFacadeAuthorisationService.assertUserHasSufficientPermissionsToChangeAttributes(
        sanitizedTenantDTO, existingTenant);
    var updatedEntity = tenantConverter.toEntity(existingTenant, sanitizedTenantDTO);
    log.info("Tenant with id {} updated", existingTenant.getId());
    setContentActivationDates(updatedEntity, sanitizedTenantDTO);
    updatedEntity = tenantService.update(updatedEntity);
    return tenantConverter.toMultilingualDTO(updatedEntity);
  }

  private void setContentActivationDates(TenantEntity entity, MultilingualTenantDTO tenantDTO) {
    MultilingualContent content = tenantDTO.getContent();

    if (content == null) {
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
    return tenantById.isEmpty()
        ? Optional.empty()
        : Optional.of(
            tenantConverter.toDTO(
                tenantById.get(), translationService.getCurrentLanguageContext()));
  }

  private MultilingualTenantDTO getConvertedAndEnrichedTenant(Optional<TenantEntity> tenantById) {
    TenantEntity tenant = tenantById.get();
    var multilingualTenantDTO = tenantConverter.toMultilingualDTO(tenant);
    enrichWithAdminDataIfSuperadmin(multilingualTenantDTO);
    enrichWithConsultingTypeSettings(multilingualTenantDTO, tenant.getId());

    return multilingualTenantDTO;
  }

  private void enrichWithConsultingTypeSettings(
      MultilingualTenantDTO multilingualTenantDTO, Long tenantId) {
    FullConsultingTypeResponseDTO consultingTypesByTenantId =
        consultingTypeService.getConsultingTypesByTenantId(tenantId.intValue());
    if (consultingTypesByTenantId != null) {
      multilingualTenantDTO
          .getSettings()
          .setExtendedSettings(
              tenantExtendedSettingsConverter.convertExtendedTenantSettings(
                  consultingTypesByTenantId));
    }
  }

  private void enrichWithAdminDataIfSuperadmin(MultilingualTenantDTO multilingualTenantDTO) {
    if (authorisationService.hasAuthority(AuthorityValue.GET_TENANT_ADMIN_DATA)) {
      enrichWithAdminData(
          multilingualTenantDTO.getId().intValue(), multilingualTenantDTO::setAdminEmails);
    }
  }

  private void enrichWithAdminData(
      final Integer tenantId, final Consumer<List<String>> setAdminEmailsConsumer) {
    List<AdminResponseDTO> tenantAdmins = userAdminService.getTenantAdmins(tenantId);
    if (tenantAdmins != null && !tenantAdmins.isEmpty()) {
      log.debug("Enriching tenant with admin email data");
      setAdminEmailsConsumer.accept(getAdminEmails(tenantAdmins));
    } else {
      log.debug("No tenant admins found for a given tenant {}", tenantId);
    }
  }

  private List<String> getAdminEmails(List<AdminResponseDTO> tenantAdmins) {
    return tenantAdmins.stream()
        .map(admin -> admin.getEmbedded() != null ? admin.getEmbedded().getEmail() : "")
        .collect(Collectors.toList());
  }

  public Optional<MultilingualTenantDTO> findMultilingualTenantById(Long id) {
    tenantFacadeAuthorisationService.assertUserIsAuthorizedToAccessTenant(id);
    var tenantById = tenantService.findTenantById(id);
    return tenantById.isEmpty()
        ? Optional.empty()
        : Optional.of(getConvertedAndEnrichedTenant(tenantById));
  }

  public Optional<RestrictedTenantDTO> findRestrictedTenantById(Long id) {
    var tenantById = tenantService.findTenantById(id);

    String lang = translationService.getCurrentLanguageContext();
    return tenantById.isEmpty()
        ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantById.get(), lang));
  }

  public List<BasicTenantLicensingDTO> getAllTenants() {
    var tenantEntities = tenantService.getAllTenants();
    return tenantEntities.stream()
        .map(tenantConverter::toBasicLicensingTenantDTO)
        .collect(Collectors.toList());
  }

  public Optional<RestrictedTenantDTO> findTenantBySubdomain(
      String subdomain, Long optionalTenantIdOverride) {
    var tenantBySubdomain = tenantService.findTenantBySubdomain(subdomain);

    Optional<Long> tenantIdFromRequestOrCookie =
        authorisationService.resolveTenantFromRequest(optionalTenantIdOverride);
    if (multitenancyWithSingleDomain && tenantIdFromRequestOrCookie.isPresent()) {
      return getTenantDataWithOverridenPrivacy(
          tenantBySubdomain, tenantIdFromRequestOrCookie.get());
    }

    String lang = translationService.getCurrentLanguageContext();
    return tenantBySubdomain.isEmpty()
        ? Optional.empty()
        : Optional.of(tenantConverter.toRestrictedTenantDTO(tenantBySubdomain.get(), lang));
  }

  public Optional<RestrictedTenantDTO> getTenantDataWithOverridenPrivacy(
      Optional<TenantEntity> mainTenantForSingleDomainMultitenancy, Long resolvedTenantId) {

    Optional<TenantEntity> tenantToOverridePrivacy = tenantService.findTenantById(resolvedTenantId);
    if (tenantToOverridePrivacy.isEmpty()) {
      throw new BadRequestException("Tenant not found for id " + resolvedTenantId);
    }
    String lang = translationService.getCurrentLanguageContext();
    RestrictedTenantDTO restrictedTenantDTO =
        tenantConverter.toRestrictedTenantDTO(mainTenantForSingleDomainMultitenancy.get(), lang);
    RestrictedTenantDTO overridingRestrictedTenantDTO =
        tenantConverter.toRestrictedTenantDTO(tenantToOverridePrivacy.get(), lang);

    if (overridingRestrictedTenantDTO.getContent() != null) {
      restrictedTenantDTO
          .getContent()
          .setPrivacy(overridingRestrictedTenantDTO.getContent().getPrivacy());
    }
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

  public boolean canAccessTenant() {
    Optional<String> subdomain = subdomainExtractor.getCurrentSubdomain();
    if (subdomain.isEmpty()) {
      return false;
    }
    var tenantBySubdomain = tenantService.findTenantBySubdomain(subdomain.get());
    return tenantFacadeAuthorisationService.canAccessTenant(tenantBySubdomain);
  }

  public Map<String, Object> findTenantsExceptTechnicalByInfix(
      String infix, int pageNumber, Integer pageSize, String fieldName, boolean isAscending) {
    var direction = isAscending ? Direction.ASC : Direction.DESC;
    var pageRequest = PageRequest.of(pageNumber, pageSize, direction, fieldName);
    Page<TenantBase> tenantPage = tenantService.findAllExceptTechnicalByInfix(infix, pageRequest);
    var tenantIds = tenantPage.stream().map(TenantBase::getId).collect(Collectors.toList());
    var fullTenants = tenantService.findAllByIds(tenantIds);
    return mapOf(tenantPage, fullTenants);
  }

  public List<AdminTenantDTO> getAllAdminTenantsExceptTechnical() {
    var tenantEntities = tenantService.getAllTenants();
    excludeTechnicalTenantFrom(tenantEntities);
    List<AdminTenantDTO> adminTenantDTOS =
        tenantEntities.stream().map(tenantConverter::toAdminTenantDTO).collect(Collectors.toList());
    adminTenantDTOS.forEach(
        adminTenantDTO ->
            enrichWithAdminData(adminTenantDTO.getId().intValue(), adminTenantDTO::setAdminEmails));
    return adminTenantDTOS;
  }

  private void excludeTechnicalTenantFrom(List<TenantEntity> tenants) {
    emptyIfNull(tenants).removeIf(tenant -> tenant.getId() == TECHNICAL_TENANT_ID);
  }

  private Map<String, Object> mapOf(Page<TenantBase> tenantPage, List<TenantEntity> fullTenants) {
    var fullTenantsLookupMap =
        fullTenants.stream().collect(Collectors.toMap(TenantEntity::getId, Function.identity()));

    var tenants = new ArrayList<Map<String, Object>>();
    tenantPage.forEach(
        tenantBase -> {
          var fullTenant = fullTenantsLookupMap.get(tenantBase.getId());
          var tenantMap = mapOf(tenantBase, fullTenant);
          tenants.add(tenantMap);
        });

    return Map.of(
        "totalElements",
        (int) tenantPage.getTotalElements(),
        "isFirstPage",
        tenantPage.isFirst(),
        "isLastPage",
        tenantPage.isLast(),
        "tenants",
        tenants);
  }

  private Map<String, Object> mapOf(TenantBase tenantBase, TenantEntity fullTenant) {
    Map<String, Object> map = new HashMap<>();
    map.put("id", tenantBase.getId());
    map.put("name", tenantBase.getName());
    map.put("subdomain", fullTenant.getSubdomain());
    map.put("beraterCount", fullTenant.getLicensingAllowedNumberOfUsers());
    List<AdminResponseDTO> tenantAdmins =
        userAdminService.getTenantAdmins(tenantBase.getId().intValue());
    map.put("adminEmails", getAdminEmails(tenantAdmins));
    map.put(
        "createDate",
        nonNull(fullTenant.getCreateDate()) ? fullTenant.getCreateDate().toString() : null);
    map.put(
        "createDate",
        nonNull(fullTenant.getUpdateDate()) ? fullTenant.getUpdateDate().toString() : null);

    return map;
  }
}
