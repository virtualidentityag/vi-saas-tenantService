package com.vi.tenantservice.api.service;

import static com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason.SUBDOMAIN_NOT_UNIQUE;

import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.model.TenantEntity;
import com.vi.tenantservice.api.model.TenantEntity.TenantBase;
import com.vi.tenantservice.api.repository.TenantRepository;
import com.vi.tenantservice.api.service.consultingtype.ApplicationSettingsService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomain;

  private final @NonNull TenantRepository tenantRepository;

  private final @NonNull ApplicationSettingsService applicationSettingsService;

  public TenantEntity create(TenantEntity tenantEntity) {
    validateTenant(tenantEntity);
    overrideSubdomainIfNeededForSingleDomainMultitenancy(tenantEntity);
    setCreateAndUpdateDate(tenantEntity);
    return tenantRepository.save(tenantEntity);
  }

  private boolean shouldOverrideSubdomain(TenantEntity tenantEntity, String mainTenantSubdomain) {
    return tenantEntity.getSubdomain() == null
        || !tenantEntity.getSubdomain().equals(mainTenantSubdomain);
  }

  private void overrideSubdomainIfNeededForSingleDomainMultitenancy(TenantEntity tenantEntity) {
    if (multitenancyWithSingleDomain) {
      String mainTenantSubdomain =
          applicationSettingsService
              .getApplicationSettings()
              .getMainTenantSubdomainForSingleDomainMultitenancy()
              .getValue();
      if (shouldOverrideSubdomain(tenantEntity, mainTenantSubdomain)) {
        tenantEntity.setSubdomain(StringUtils.EMPTY);
      }
    }
  }

  private void setCreateAndUpdateDate(TenantEntity tenantEntity) {
    tenantEntity.setCreateDate(LocalDateTime.now(ZoneOffset.UTC));
    tenantEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
  }

  private void validateTenantSubdomainDoesNotExist(TenantEntity tenantEntity) {
    var dbTenant = tenantRepository.findBySubdomain(tenantEntity.getSubdomain());
    if (tenantWithSuchSubdomainAlreadyExists(tenantEntity, dbTenant)) {
      throw new TenantValidationException(SUBDOMAIN_NOT_UNIQUE);
    }
  }

  private boolean tenantWithSuchSubdomainAlreadyExists(
      TenantEntity tenantEntity, TenantEntity dbTenant) {
    return dbTenant != null && !dbTenant.getId().equals(tenantEntity.getId());
  }

  public TenantEntity update(TenantEntity tenantEntity) {
    validateTenant(tenantEntity);
    overrideSubdomainIfNeededForSingleDomainMultitenancy(tenantEntity);
    tenantEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
    return tenantRepository.save(tenantEntity);
  }

  private void validateTenant(TenantEntity tenantEntity) {
    if (!multitenancyWithSingleDomain) {
      validateTenantSubdomainDoesNotExist(tenantEntity);
    }
  }

  public Optional<TenantEntity> findTenantById(Long id) {
    return tenantRepository.findById(id);
  }

  public Optional<TenantEntity> findTenantBySubdomain(String subdomain) {
    var bySubdomain = tenantRepository.findBySubdomain(subdomain);
    return bySubdomain != null ? Optional.of(bySubdomain) : Optional.empty();
  }

  public List<TenantEntity> getAllTenants() {
    return tenantRepository.findAll();
  }

  public Page<TenantBase> findAllExceptTechnicalByInfix(String infix, PageRequest pageRequest) {
    return tenantRepository.findAllExceptTechnicalByInfix(infix, pageRequest);
  }

  public List<TenantEntity> findAllByIds(List<Long> tenantIds) {
    return tenantRepository.findAllByIdIn(tenantIds);
  }
}
