package com.vi.tenantservice.api.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.tenantservice.api.exception.TenantValidationException;
import com.vi.tenantservice.api.exception.httpresponse.HttpStatusExceptionReason;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantFacadeValidationService {

  public void validate(TenantDTO tenantDTO) {
    validateSettings(tenantDTO);
  }

  private void validateSettings(TenantDTO tenantDTO) {
    try {
      if (tenantHasSettings(tenantDTO)) {
        tryDeserializeToJson(tenantDTO);
      }
    } catch (JsonProcessingException ex) {
      logException(tenantDTO, ex);
      throw new TenantValidationException(HttpStatusExceptionReason.INVALID_SETTINGS_VALUE, HttpStatus.BAD_REQUEST);
    }
  }

  private void tryDeserializeToJson(TenantDTO tenantDTO) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.readValue(tenantDTO.getSettings(), TenantSettings.class);
  }

  private void logException(TenantDTO tenantDTO, JsonProcessingException ex) {
    log.warn(
        "Tenant settings validation failed. Could not deserialize settings value to JSON ", tenantDTO.getSettings());
    log.debug(ex.getMessage());
  }

  private boolean tenantHasSettings(TenantDTO tenantDTO) {
    return tenantDTO.getSettings() != null;
  }
}
