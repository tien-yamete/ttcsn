package com.tien.apigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.apigateway.dto.ApiResponse;
import com.tien.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Bộ lọc xác thực token JWT trước khi cho request đi tiếp
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final int UNAUTHENTICATED_CODE = 1401;
    private static final String UNAUTHENTICATED_MESSAGE = "Unauthenticated";
    private static final String BEARER_PREFIX = "Bearer ";

    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = {
            "/identity/auth/.*",
            "/identity/oauth2/.*",
            "/identity/login/oauth2/.*",
            "/notification/email/send",
            "/file/media/download/.*"
    };

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.debug("Đang xử lý request: {}", path);

        if (isPublicEndpoint(exchange.getRequest())) {
            log.debug("Endpoint công khai, bỏ qua xác thực");
            return chain.filter(exchange);
        }

        List<String> authHeaders = exchange.getRequest()
                .getHeaders()
                .get(HttpHeaders.AUTHORIZATION);

        if (CollectionUtils.isEmpty(authHeaders)) {
            log.warn("Thiếu Authorization header cho path: {}", path);
            return unauthenticated(exchange.getResponse());
        }

        String authHeader = authHeaders.get(0);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Định dạng Authorization header không hợp lệ");
            return unauthenticated(exchange.getResponse());
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            log.warn("Token rỗng");
            return unauthenticated(exchange.getResponse());
        }

        log.debug("Đang xác thực token");

        return identityService.introspect(token)
                .flatMap(apiResponse -> {
                    if (apiResponse != null
                            && apiResponse.getResult() != null
                            && apiResponse.getResult().isValid()) {
                        log.debug("Token hợp lệ");
                        return chain.filter(exchange);
                    }
                    log.warn("Token không hợp lệ");
                    return unauthenticated(exchange.getResponse());
                })
                .onErrorResume(throwable -> {
                    log.error("Lỗi khi xác thực token", throwable);
                    return unauthenticated(exchange.getResponse());
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return Arrays.stream(publicEndpoints)
                .anyMatch(pattern -> path.matches(apiPrefix + pattern));
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(UNAUTHENTICATED_CODE)
                .message(UNAUTHENTICATED_MESSAGE)
                .build();

        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            log.error("Không thể serialize unauthenticated response", e);
            body = String.format("{\"code\":%d,\"message\":\"%s\"}", UNAUTHENTICATED_CODE, UNAUTHENTICATED_MESSAGE);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}
