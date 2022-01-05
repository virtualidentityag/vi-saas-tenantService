package com.vi.tenantservice.config.security;


import com.vi.tenantservice.api.config.SpringFoxConfig;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

/**
 * Configuration class to provide the keycloak security configuration.
 */
@KeycloakConfiguration
@EnableGlobalMethodSecurity(
        prePostEnabled = true)
@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Configures the basic http security behavior.
     *
     * @param http springs http security
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authenticationProvider(keycloakAuthenticationProvider())
                .addFilterBefore(keycloakAuthenticationProcessingFilter(), BasicAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                .and()
                .authorizeRequests()
                .requestMatchers(new AntPathRequestMatcher("/tenant")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/tenant/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/public/tenant/**")).permitAll()
                .antMatchers(SpringFoxConfig.WHITE_LIST).permitAll()
                .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant"))).permitAll()
                .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/tenant/**"))).permitAll()
                .and()
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'")
                .and();
    }


    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    /**
     * Provides the keycloak configuration resolver bean.
     *
     * @return the configured {@link KeycloakConfigResolver}
     */
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

}
