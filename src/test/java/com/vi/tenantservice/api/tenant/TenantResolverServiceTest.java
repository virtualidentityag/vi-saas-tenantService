package com.vi.tenantservice.api.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TenantResolverServiceTest {

    @Mock
    AccessTokenTenantResolver accessTokenTenantResolver;

    @InjectMocks
    TenantResolverService tenantResolverService;

    @Mock
    HttpServletRequest request;

    @Test
    void tryResolve_Should_Call_AccessTokenResolver_ToResolveTenant() {
        // when
        tenantResolverService.tryResolve(request);
        // then
        Mockito.verify(accessTokenTenantResolver).resolve(request);
    }

}