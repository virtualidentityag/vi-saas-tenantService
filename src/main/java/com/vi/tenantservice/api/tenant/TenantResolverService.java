package com.vi.tenantservice.api.tenant;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantResolverService {

  @NonNull AccessTokenTenantResolver accessTokenTenantResolver;

  @NonNull CookieTenantResolver cookieTenantResolver;

  public Optional<Long> tryResolve() {
    HttpServletRequest request = getHttpServletRequest();
    if (userIsAuthenticated(request)) {
      return accessTokenTenantResolver.resolve(request);
    } else {
      return tryResolveFromCookie(request);
    }
  }

  private static HttpServletRequest getHttpServletRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
  }

  public Optional<Long> tryResolveFromCookie() {
    return tryResolveFromCookie(getHttpServletRequest());
  }

  private Optional<Long> tryResolveFromCookie(HttpServletRequest request) {
    return cookieTenantResolver.resolveTenantFromRequest(request);
  }

  private boolean userIsAuthenticated(HttpServletRequest request) {
    return request.getUserPrincipal() != null;
  }
}
