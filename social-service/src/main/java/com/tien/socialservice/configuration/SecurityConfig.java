package com.tien.socialservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * SecurityConfig: Cấu hình bảo mật cho toàn bộ ứng dụng.
 * - Định nghĩa các endpoint public (không cần xác thực)
 * - Cấu hình OAuth2 Login (Google)
 * - Cấu hình JWT Resource Server để xác thực token
 * - Cấu hình password encoder (BCrypt)
 * - Convert JWT claims thành Spring Security authorities
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    // Các endpoint public không yêu cầu xác thực
    private final String[] PUBLIC_ENDPOINTS = {"/internal/**"};
    // Swagger UI endpoints
    private final String[] SWAGGER_ENDPOINTS = {
        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
    };

    CustomJwtDecoder customJwtDecoder;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình phân quyền cho các request
        // permitAll vẫn decode JWT nếu có, chỉ bỏ qua authorization check
        httpSecurity.authorizeHttpRequests(requests -> requests.requestMatchers(PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(SWAGGER_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());
        // Cấu hình Resource Server với JWT
        // JWT vẫn được decode ngay cả với permitAll endpoints
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(jwtAuthenticationEntryPoint));
        // Tắt CSRF (không cần cho API REST stateless)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    // Convert JWT claim "scope" hoặc "roles" thành GrantedAuthority trong Spring Security.
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Converter mặc định lấy "scope" trong token -> authority
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // Xóa prefix "SCOPE_" mặc định

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
