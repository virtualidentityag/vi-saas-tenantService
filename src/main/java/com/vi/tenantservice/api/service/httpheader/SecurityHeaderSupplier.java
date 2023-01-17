package com.vi.tenantservice.api.service.httpheader;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHeaderSupplier {

  @Value("${csrf.header.property}")
  private String csrfHeaderProperty;

  @Value("${csrf.cookie.property}")
  private String csrfCookieProperty;

  /**
   * Returns a {@link HttpHeaders} instance with needed settings for the services API (CSRF Token).
   *
   * @return {@link HttpHeaders}
   */
  public HttpHeaders getCsrfHttpHeaders() {
    var httpHeaders = new HttpHeaders();

    return this.addCsrfValues(httpHeaders);
  }

  public HttpHeaders getKeycloakAndCsrfHttpHeaders() {
    var header = getCsrfHttpHeaders();
    this.addKeycloakAuthorizationHeader(
        header, getPrincipal().getKeycloakSecurityContext().getTokenString());
    return header;
  }

  private void addKeycloakAuthorizationHeader(HttpHeaders httpHeaders, String accessToken) {
    httpHeaders.add("Authorization", "Bearer " + accessToken);
  }

  private KeycloakPrincipal getPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (KeycloakPrincipal) authentication.getPrincipal();
  }

  private HttpHeaders addCsrfValues(HttpHeaders httpHeaders) {
    var csrfToken = UUID.randomUUID().toString();

    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.add("Cookie", csrfCookieProperty + "=" + csrfToken);
    httpHeaders.add(csrfHeaderProperty, csrfToken);

    return httpHeaders;
  }
}
