package com.vi.tenantservice.api.tenant;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantResolverServiceTest {

  @Mock AccessTokenTenantResolver accessTokenTenantResolver;

  @InjectMocks TenantResolverService tenantResolverService;

  @Mock HttpServletRequest request;

  @Test
  void tryResolve_Should_Call_AccessTokenResolver_ToResolveTenant() {
    // when
    tenantResolverService.tryResolve(request);
    // then
    Mockito.verify(accessTokenTenantResolver).resolve(request);
  }
}
