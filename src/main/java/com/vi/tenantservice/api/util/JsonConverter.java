package com.vi.tenantservice.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.google.common.collect.Lists;
import com.vi.tenantservice.api.model.TenantSettings;
import com.vi.tenantservice.api.model.Translation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonConverter {

  public static String convertToJson(Object object) {
    return serializeToJsonString(object);
  }

  public static String convertToJson(List<Translation> translations) {
    if (translations == null || translations.isEmpty()) {
      return null;
    }
    return serializeToJsonString(translations);
  }

  public static TenantSettings convertFromJson(String jsonString) {
    return deserializeFromJsonString(jsonString, TenantSettings.class);
  }


  public static <T> List<T> convertListFromJson(String jsonString) {
    if (jsonString == null) {
      return Lists.newArrayList();
    }
    return deserializeFromJsonString(jsonString,  new TypeReference<List<T>>() { });
  }

  private static <T> T deserializeFromJsonString(String jsonString, Class<T> clazz) {
    try {
      var objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return objectMapper.readValue(jsonString, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }
  }

  private static <T> List<T> deserializeFromJsonString(String jsonString, TypeReference<List<T>> typeReference) {
    try {
      var objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return objectMapper.readValue(jsonString, typeReference);
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
