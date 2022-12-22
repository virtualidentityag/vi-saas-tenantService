package com.vi.tenantservice.api.tenant;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantResolverService {

  @NonNull
  AccessTokenTenantResolver accessTokenTenantResolver;

  public Optional<Long> tryResolve(HttpServletRequest request) {
      return accessTokenTenantResolver.resolve(request);
  }

}
