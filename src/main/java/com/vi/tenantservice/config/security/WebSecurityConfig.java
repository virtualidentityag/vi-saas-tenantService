package com.vi.tenantservice.config.security;

import static org.springframework.security.config.Customizer.withDefaults;

import com.vi.tenantservice.api.config.SpringFoxConfig;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

/** Configuration class to provide the keycloak security configuration. */
@KeycloakConfiguration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  @Autowired JwtAuthConverterProperties jwtAuthConverterProperties;

  @Autowired AuthorisationService authorisationService;

  @Bean
  public JwtAuthConverter jwtAuthConverter() {
    return new JwtAuthConverter(jwtAuthConverterProperties, authorisationService);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            requests ->
                requests
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
                    .requestMatchers(
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant")))
                    .permitAll()
                    .requestMatchers(
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant/**")))
                    .permitAll())
        .headers(
            headers ->
                headers
                    .xssProtection(withDefaults())
                    .contentSecurityPolicy(policy -> policy.policyDirectives("script-src 'self'")));
    http.oauth2ResourceServer(
        server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));

    return http.build();
  }
}
