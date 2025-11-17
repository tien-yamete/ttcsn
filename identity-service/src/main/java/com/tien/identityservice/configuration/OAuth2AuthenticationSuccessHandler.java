
package com.tien.identityservice.configuration;

import com.tien.identityservice.constant.SignInProvider;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.UserRepository;
import com.tien.identityservice.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    final AuthenticationService authenticationService;
    final UserRepository userRepository;

    @NonFinal
    @Value("${app.oauth2.authorizedRedirectUri}")
    String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String name = (String) principal.getAttributes().get("name");
        String providerUserId = principal.getName(); // Google's sub/id

        if (email == null || email.isEmpty()) {
            String fail = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "EMAIL_NOT_PROVIDED")
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, fail);
            return;
        }

        // Get or create user from Google OAuth
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Create new user and profile from Google OAuth
            user = authenticationService.createUserFromGoogleOAuth(email, name, providerUserId);
        } else {
            // Update provider info if needed
            if (user.getProvider() == null || user.getProvider() != SignInProvider.GOOGLE) {
                user.setProvider(SignInProvider.GOOGLE);
                user.setProviderUserId(providerUserId);
                user.setEmailVerified(true);
                user.setIsActive(true);
                user = userRepository.save(user);
            }
        }

        // Fetch user with roles and permissions to avoid LazyInitializationException
        User userWithRoles = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElse(user);

        String token = authenticationService.generateToken(userWithRoles);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}