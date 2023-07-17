package com.vi.tenantservice.api.tenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class AccessTokenTenantResolver implements TenantResolver {

  private static final String TENANT_ID = "tenantId";

  @Override
  public Optional<Long> resolve(HttpServletRequest request) {
    return resolveTenantIdFromTokenClaims(request);
  }

  private Optional<Long> resolveTenantIdFromTokenClaims(HttpServletRequest request) {
    Map<String, Object> claimMap = getClaimMap(request);
    log.debug("Found tenantId in claim : " + claimMap.toString());
    return getUserTenantIdAttribute(claimMap);
  }

  private Optional<Long> getUserTenantIdAttribute(Map<String, Object> claimMap) {
    if (claimMap.containsKey(TENANT_ID)) {
      Long tenantId = (Long) claimMap.get(TENANT_ID);
      return Optional.of(tenantId);
    } else {
      return Optional.empty();
    }
  }

  private Map<String, Object> getClaimMap(HttpServletRequest request) {
    var jwt = ((JwtAuthenticationToken) request.getUserPrincipal()).getToken();
    return jwt.getClaims();
  }

  @Override
  public boolean canResolve(HttpServletRequest request) {
    return resolve(request).isPresent();
  }
}
