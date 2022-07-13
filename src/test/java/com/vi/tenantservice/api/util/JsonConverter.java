package com.vi.tenantservice.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.vi.tenantservice.api.model.TenantDTO;
import com.vi.tenantservice.api.model.TenantSettings;

public class JsonConverter {
    public static String convertToJson(TenantDTO tenantDTO) {
        return serializeToJsonString(tenantDTO);
    }

    public static String convertToJson(TenantSettings tenantSettings) {
        return serializeToJsonString(tenantSettings);
    }

    private static <T> String serializeToJsonString(T object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeJsonMappingException(e.getMessage());
        }
    }
}
