package com.tien.identity_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Các endpoint public không yêu cầu xác thực
    private final String[] PUBLIC_ENPOINTS = {
        "/users/registration", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình phân quyền cho các request
        httpSecurity.authorizeHttpRequests(requests -> requests.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS)
                .permitAll()
                // .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()).hasAuthority("ROLE_ADMIN")
                .anyRequest()
                .authenticated()); // Các request khác phải xác thực

        // Cấu hình Resource Server với JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
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

    // Password encoder sử dụng BCrypt.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
