package com.vi.tenantservice.config.security;

import com.vi.tenantservice.api.config.SpringFoxConfig;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

/** Configuration class to provide the keycloak security configuration. */
@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        //        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
        .and()
        .authorizeRequests()
        .requestMatchers(new AntPathRequestMatcher("/tenant"))
        .authenticated()
        .requestMatchers(new AntPathRequestMatcher("/tenant/*"))
        .authenticated()
        .requestMatchers(new AntPathRequestMatcher("/tenantadmin"))
        .authenticated()
        .requestMatchers(new AntPathRequestMatcher("/tenantadmin/*"))
        .authenticated()
        .requestMatchers(new AntPathRequestMatcher("/tenant/public/**"))
        .permitAll()
        .requestMatchers(SpringFoxConfig.WHITE_LIST)
        .permitAll()
        .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant")))
        .permitAll()
        .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant/**")))
        .permitAll()
        .and()
        .headers()
        .xssProtection()
        .and()
        .contentSecurityPolicy("script-src 'self'");
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt());

    return http.build();
  }
}
