package com.vi.tenantservice.config.security;

import java.util.Optional;
import java.util.Set;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

  public boolean hasAuthority(String authorityName) {
    return getAuthentication().getAuthorities().stream()
        .anyMatch(role -> authorityName.equals(role.getAuthority()));
  }

  public boolean hasRole(String roleName) {
    Set<String> roles =
        getPrincipal().getKeycloakSecurityContext().getToken().getRealmAccess().getRoles();
    return roles != null && roles.contains(roleName);
  }

  public Optional<Long> findTenantIdInAccessToken() {
    Integer tenantId =
        (Integer)
            getPrincipal().getKeycloakSecurityContext().getToken().getOtherClaims().get("tenantId");
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

  private KeycloakPrincipal<?> getPrincipal() {
    return (KeycloakPrincipal) getAuthentication().getPrincipal();
  }
}
