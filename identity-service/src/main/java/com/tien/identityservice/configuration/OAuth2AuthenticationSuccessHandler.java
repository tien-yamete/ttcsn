
package com.tien.identityservice.configuration;

import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.UserRepository;
import com.tien.identityservice.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    AuthenticationService authenticationService;

    final UserRepository userRepository;

    @Value("${app.oauth2.authorizedRedirectUri}")
    String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            String fail = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "USER_NOT_FOUND")
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, fail);
            return;
        }

        String token = authenticationService.generateToken(user); // <-- dùng chung cách tạo token
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}