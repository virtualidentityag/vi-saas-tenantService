package com.vi.tenantservice.config.security;

import io.swagger.models.HttpMethod;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiTokenFilter extends OncePerRequestFilter {

  @Value("${external.user.create.tenant.api.token}")
  private String externalUserCreateTenantApiToken;

  public ApiTokenFilter() {
    //Empty constructor
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();
    return !path.endsWith("/tenantadmin") || !method.equalsIgnoreCase(HttpMethod.POST.toString());
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String token = request.getHeader("Authorization");

    if (validateExternalUserCreateTenantApiToken(token)){
        // Create an authentication token
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "ExternalTechnicalAdmin", null,
            Collections.singletonList(new SimpleGrantedAuthority("AUTHORIZATION_CREATE_TENANT")));
        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  private boolean validateExternalUserCreateTenantApiToken(String token) {
    return Objects.equals(externalUserCreateTenantApiToken, token);
  }
}
