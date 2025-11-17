package com.tien.identityservice.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.tien.identityservice.constant.SignInProvider;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.UserRepository;
import com.tien.identityservice.service.JwtService;
import com.tien.identityservice.service.OAuth2Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * OAuth2AuthenticationSuccessHandler: Xử lý khi OAuth2 login thành công
 * - Lấy thông tin user từ OAuth2 provider (email, name, providerUserId)
 * - Tạo hoặc lấy user từ database
 * - Tạo JWT token cho user
 * - Redirect về frontend với token trong query parameter
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    JwtService jwtService;

    UserRepository userRepository;

    OAuth2Service oAuth2Service;

    @NonFinal
    @Value("${app.oauth2.authorizedRedirectUri}")
    String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String name = (String) principal.getAttributes().get("name");
        String providerUserId = principal.getName(); // Google's sub/id

        if (email == null || email.isEmpty()) {
            String fail = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "EMAIL_NOT_PROVIDED")
                    .build()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, fail);
            return;
        }

        // Get or create user from OAuth2 provider (currently Google)
        // TODO: Detect provider from request/authentication when supporting multiple providers
        User user = oAuth2Service.getOrCreateUserFromOAuth(email, name, providerUserId, SignInProvider.GOOGLE);

        // Fetch user with roles and permissions to avoid LazyInitializationException
        User userWithRoles =
                userRepository.findByIdWithRolesAndPermissions(user.getId()).orElse(user);

        String token = jwtService.generateToken(userWithRoles);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
