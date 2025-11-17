package com.tien.identityservice.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2AuthenticationFailureHandler: Xử lý khi OAuth2 login thất bại.
 * - Lấy thông tin lỗi từ OAuth2 provider hoặc exception
 * - Redirect về frontend với error message trong query parameter
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.authorizedRedirectUri}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        // Get error from request parameters (from Google OAuth redirect)
        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");

        // Use error from request if available, otherwise use exception message
        String errorMessage = error != null ? error : exception.getLocalizedMessage();

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", errorMessage)
                .queryParamIfPresent("error_description", java.util.Optional.ofNullable(errorDescription))
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
