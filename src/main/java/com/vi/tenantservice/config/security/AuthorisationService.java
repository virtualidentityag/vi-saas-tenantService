package com.vi.tenantservice.config.security;

import java.util.Optional;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

    public boolean hasAuthority(String authorityName) {
        return getAuthentication().getAuthorities().stream().filter(role -> authorityName.equals(role.getAuthority())).findAny().isPresent();
    }

    public Optional<Long> findCustomUserAttributeInAccessToken(String attributeName) {
        Integer tenantId = (Integer) getPrincipal().getKeycloakSecurityContext().getToken().getOtherClaims().get(attributeName);
        if (tenantId == null) {
            throw new AccessDeniedException("tenantId attribute not found in the access token");
        }
        return Optional.ofNullable(Long.valueOf(tenantId));
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
}
