package com.tien.chatservice.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    CustomJwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            // Xử lý authentication cho CONNECT
            if (StompCommand.CONNECT.equals(command)) {
                List<String> authHeaders = accessor.getNativeHeader("Authorization");

                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String authHeader = authHeaders.get(0);

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        try {
                            Jwt jwt = jwtDecoder.decode(token);

                            List<SimpleGrantedAuthority> grantedAuthorities = List.of();
                            Object scopeClaim = jwt.getClaims().get("scope");

                            if (scopeClaim instanceof String) {
                                String scope = (String) scopeClaim;
                                if (!scope.isEmpty()) {
                                    grantedAuthorities = Arrays.stream(scope.split("\\s+"))
                                            .filter(s -> !s.isEmpty())
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
                                }
                            } else if (scopeClaim instanceof List) {
                                @SuppressWarnings("unchecked")
                                List<String> scopes = (List<String>) scopeClaim;
                                grantedAuthorities = scopes.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());
                            }

                            Authentication authentication = new JwtAuthenticationToken(jwt, grantedAuthorities);
                            accessor.setUser(authentication);
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.info("Kết nối WebSocket đã được xác thực cho user: {}", jwt.getSubject());
                        } catch (Exception e) {
                            log.error("Xác thực kết nối WebSocket thất bại: {}", e.getMessage());
                            throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
                        }
                    } else {
                        log.warn("Cố gắng kết nối WebSocket với định dạng Authorization header không hợp lệ");
                        throw new MessageDeliveryException("Invalid Authorization header format");
                    }
                } else {
                    log.warn("Cố gắng kết nối WebSocket mà không có Authorization header");
                    throw new MessageDeliveryException("Authorization header is required");
                }
            } else if (command != null
                    && (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command))) {
                // Với các message khác (SEND, SUBSCRIBE), lấy Authentication từ Principal đã được set khi CONNECT
                // và set vào SecurityContextHolder để service có thể sử dụng
                // (Quan trọng: SecurityContextHolder là ThreadLocal, cần set lại cho mỗi message)
                Object userPrincipal = accessor.getUser();
                if (userPrincipal instanceof Authentication) {
                    Authentication auth = (Authentication) userPrincipal;
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug(
                            "Đã set Authentication vào SecurityContextHolder cho message {} từ user: {}",
                            command,
                            auth.getName());
                } else {
                    // Nếu không có Authentication từ session, thử lấy từ header
                    List<String> authHeaders = accessor.getNativeHeader("Authorization");
                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        String authHeader = authHeaders.get(0);
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            try {
                                Jwt jwt = jwtDecoder.decode(token);
                                List<SimpleGrantedAuthority> grantedAuthorities = List.of();
                                Object scopeClaim = jwt.getClaims().get("scope");

                                if (scopeClaim instanceof String) {
                                    String scope = (String) scopeClaim;
                                    if (!scope.isEmpty()) {
                                        grantedAuthorities = Arrays.stream(scope.split("\\s+"))
                                                .filter(s -> !s.isEmpty())
                                                .map(SimpleGrantedAuthority::new)
                                                .collect(Collectors.toList());
                                    }
                                } else if (scopeClaim instanceof List) {
                                    @SuppressWarnings("unchecked")
                                    List<String> scopes = (List<String>) scopeClaim;
                                    grantedAuthorities = scopes.stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
                                }

                                Authentication authentication = new JwtAuthenticationToken(jwt, grantedAuthorities);
                                accessor.setUser(authentication);
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug(
                                        "Đã set Authentication từ header vào SecurityContextHolder cho message {} từ user: {}",
                                        command,
                                        jwt.getSubject());
                            } catch (Exception e) {
                                log.error("Xác thực WebSocket message thất bại: {}", e.getMessage());
                                throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
                            }
                        } else {
                            log.warn("Message {} có Authorization header nhưng format không hợp lệ", command);
                            throw new MessageDeliveryException("Invalid Authorization header format");
                        }
                    } else {
                        log.warn(
                                "Message {} không có Authentication từ Principal và không có Authorization header",
                                command);
                        throw new MessageDeliveryException("Authentication required for " + command);
                    }
                }
            }
        }

        return message;
    }
}
