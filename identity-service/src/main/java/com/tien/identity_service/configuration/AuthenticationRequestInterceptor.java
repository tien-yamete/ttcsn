package com.tien.identity_service.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
