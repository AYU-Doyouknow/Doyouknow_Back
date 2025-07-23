package org.ayu.doyouknowback.config;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.config.security.APIKeyAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Value("${http.auth-token-header}")
    private String principalRequestHeader;

    @Value("${http.auth-token}")
    private String principalRequestValue;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);

        filter.setAuthenticationManager(new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String principal = (String) authentication.getPrincipal();
                if (!principalRequestValue.equals(principal)) {
                    throw new BadCredentialsException("The API key was not found or not the expected value.");
                }
                authentication.setAuthenticated(true);
                return authentication;
            }
        });

        http
                // csrf disable -> session stateless
                .csrf(AbstractHttpConfigurer::disable)

                // filter 등록
                .addFilterBefore(filter, AbstractPreAuthenticatedProcessingFilter.class)

                // Form 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)

                // 경로별 인가 작업
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/notice/addNotice").authenticated()
                        .requestMatchers("/lost/addLost").authenticated()
                        .requestMatchers("/news/addNews").authenticated()
                        .anyRequest().permitAll())

                // 세션 설정 (Session StateLess)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
