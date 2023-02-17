package com.vi.tenantservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CookieTenantResolverTest {

  @InjectMocks CookieTenantResolver cookieTenantResolver;

  @Test
  void resolve_Should_returnTenantId_When_tenantIdInCookie() {
    ReflectionTestUtils.setField(cookieTenantResolver, "multitenancyWithSingleDomain", true);
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
            + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY2NzM0NzI0MiwiZXhwIjoxNjY3MzUwODQyLCJ0ZW5hbnRJZCI6NH0."
            + "SjDBi7mbXwpUuZmUl7BEqptxsrd2aEJ6VMSIfTQx4sk";

    Optional<Long> tenantId = cookieTenantResolver.resolveTenantFromRequest(request(jwt));
    assertThat(tenantId.get()).isNotNull();
  }

  @Test
  void resolve_Should_returnNull_When_tenantIdNotInCookie() {
    ReflectionTestUtils.setField(cookieTenantResolver, "multitenancyWithSingleDomain", true);
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
            + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY2NzM0NzI0MiwiZXhwIjoxNjY3MzUwODQyfQ."
            + "rKjznZb8k-IMylStd_1qs8Qwk7-pKPq33Nax-Fj_M6w";

    Optional<Long> tenantId = cookieTenantResolver.resolveTenantFromRequest(request(jwt));
    assertThat(tenantId).isEmpty();
  }

  private MockHttpServletRequest request(String jwt) {
    var request = new MockHttpServletRequest();
    Cookie cookie = new Cookie("keycloak", jwt);
    request.setCookies(cookie);
    return request;
  }
}
