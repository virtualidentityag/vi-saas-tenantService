package com.vi.tenantservice.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;

public class JsonConverter {

  JsonConverter() {

  }

  public static String convertToJson(TenantDTO tenantDTO) {
    return serializeToJsonString(tenantDTO);
  }

  public static String convertToJson(TenantSettings tenantSettings) {
    return serializeToJsonString(tenantSettings);
  }

  public static TenantSettings convertFromJson(String jsonString) {
    return deserializeFromJsonString(jsonString, TenantSettings.class);
  }

  private static <T> T deserializeFromJsonString(String jsonString, Class<T> clazz) {
    try {
      return new ObjectMapper().readValue(jsonString, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }
  }

  private static <T> String serializeToJsonString(T object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }
  }
}
