package com.vi.tenantservice.api.tenant;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantResolverService {

  @NonNull AccessTokenTenantResolver accessTokenTenantResolver;

  public Optional<Long> tryResolve(HttpServletRequest request) {
    return accessTokenTenantResolver.resolve(request);
  }
}
