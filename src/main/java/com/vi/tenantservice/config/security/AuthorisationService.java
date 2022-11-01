package com.vi.tenantservice.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

@Service
public class AuthorisationService {

  public boolean hasAuthority(String authorityName) {
    return getAuthentication().getAuthorities().stream()
        .anyMatch(role -> authorityName.equals(role.getAuthority()));
  }


  public Optional<Long> findTenantIdInAccessToken() {
    Integer tenantId = (Integer) getPrincipal().getKeycloakSecurityContext().getToken()
        .getOtherClaims().get("tenantId");
    if (tenantId == null) {
      throw new AccessDeniedException("tenantId attribute not found in the access token");
    }
    return Optional.of(Long.valueOf(tenantId));
  }

  public Object getUsername() {
    return getPrincipal().getName();
  }

  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private KeycloakPrincipal getPrincipal() {
    return (KeycloakPrincipal) getAuthentication().getPrincipal();
  }

  public Optional<Long> resolveTenantFromRequest(Long tenantId) {
    if (tenantId != null) {
      return Optional.of(tenantId);
    }
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();
    Cookie token = WebUtils.getCookie(request, "keycloak");

    if (token == null) {
      return Optional.empty();
    }

    String[] chunks = token.getValue().split("\\.");
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String payload = new String(decoder.decode(chunks[1]));
    var objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      Map<String, Object> map = objectMapper.readValue(payload, Map.class);
      Integer tenantIdFromCookie = (Integer) map.get("tenantId");
      return tenantIdFromCookie == null ? Optional.empty()
          : Optional.of(Long.valueOf(tenantIdFromCookie));
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }

  }
}
