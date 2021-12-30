package com.vi.tenantservice.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.vi.tenantservice.api.model.TenantDTO;

public class JsonConverter {
    public static String convert(TenantDTO tenantDTO) {
        try {
            return new ObjectMapper().writeValueAsString(tenantDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeJsonMappingException(e.getMessage());
        }
    }
}
