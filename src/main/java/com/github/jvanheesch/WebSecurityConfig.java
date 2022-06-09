package com.github.jvanheesch;

import com.github.jvanheesch.adapter.UserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserServiceClient userServiceClient) throws Exception {
        http.addFilterBefore(requestHeaderAuthenticationFilter(userServiceClient), AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        DefaultSecurityFilterChain build = http.build();
        return build;
    }

    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter(UserServiceClient userServiceClient) {
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("username");
        requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager(userServiceClient));
        return requestHeaderAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserServiceClient userServiceClient) {
        return new ProviderManager(preAuthenticatedAuthenticationProvider(userServiceClient));
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(UserServiceClient userServiceClient) {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(authenticationUserDetailsService(userServiceClient));
        return preAuthenticatedAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService(UserServiceClient userServiceClient) {
        return new UserDetailsByNameServiceWrapper<>(userDetailsService(userServiceClient));
    }

    @Bean
    public UserDetailsService userDetailsService(UserServiceClient userServiceClient) {
        return username -> Optional.ofNullable(userServiceClient.getUser(username))
                .map(userDto -> "admin".equals(userDto.getName())
                        ? new User(username, "test", Stream.of("ROLE_OK", "ROLE_NOK").map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                        : new User(username, "test", Stream.of("ROLE_OK").map(SimpleGrantedAuthority::new).collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
