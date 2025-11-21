package com.tien.interactionservice.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AuthenticationRequestInterceptor: Interceptor cho Feign Client.
 * - Tự động lấy header "Authorization" từ HTTP request hiện tại
 * - Forward header này sang các Feign client calls
 * - Đảm bảo JWT token được truyền tự động giữa các microservice
 */
@Slf4j
@Component
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        log.info("Header: {}", authHeader);
        if (StringUtils.hasText(authHeader)) requestTemplate.header("Authorization", authHeader);
    }
}
