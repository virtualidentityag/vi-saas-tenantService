package com.vi.tenantservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.config.security.AuthorisationService;
import java.util.Optional;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class AuthorisationServiceTest {

  @InjectMocks AuthorisationService authorisationService;

  @Test
  public void resolve_Should_returnTenantId_When_tenantIdInCookie() {
    ReflectionTestUtils.setField(authorisationService, "multitenancyWithSingleDomain", true);
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
            + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY2NzM0NzI0MiwiZXhwIjoxNjY3MzUwODQyLCJ0ZW5hbnRJZCI6NH0."
            + "SjDBi7mbXwpUuZmUl7BEqptxsrd2aEJ6VMSIfTQx4sk";
    givenRequestContextIsSet(jwt);
    Optional<Long> tenantId = authorisationService.resolveTenantFromRequest(null);
    assertThat(tenantId.get()).isNotNull();
    resetRequestAttributes();
  }

  @Test
  public void resolve_Should_returnNull_When_tenantIdNotInCookie() {
    ReflectionTestUtils.setField(authorisationService, "multitenancyWithSingleDomain", true);
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
            + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY2NzM0NzI0MiwiZXhwIjoxNjY3MzUwODQyfQ."
            + "rKjznZb8k-IMylStd_1qs8Qwk7-pKPq33Nax-Fj_M6w";
    givenRequestContextIsSet(jwt);
    Optional<Long> tenantId = authorisationService.resolveTenantFromRequest(null);
    assertThat(tenantId.isEmpty()).isEqualTo(true);
    resetRequestAttributes();
  }

  @Test
  public void resolve_Should_returnTenantId_When_tenantIdGivenByArgument() {
    ReflectionTestUtils.setField(authorisationService, "multitenancyWithSingleDomain", true);
    givenRequestContextIsSet(null);
    Optional<Long> tenantId = authorisationService.resolveTenantFromRequest(1L);
    assertThat(tenantId.get()).isEqualTo(1L);
    resetRequestAttributes();
  }

  private void givenRequestContextIsSet(String jwt) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Cookie cookie = new Cookie("keycloak", jwt);
    request.setCookies(cookie);
    ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);
  }

  private void resetRequestAttributes() {
    RequestContextHolder.setRequestAttributes(null);
  }
}
