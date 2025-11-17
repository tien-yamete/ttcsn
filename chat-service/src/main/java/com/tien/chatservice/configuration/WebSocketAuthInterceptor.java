package com.tien.chatservice.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    CustomJwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
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
                                grantedAuthorities = java.util.Arrays.stream(scope.split("\\s+"))
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
                        
                        log.info("WebSocket connection authenticated for user: {}", jwt.getSubject());
                    } catch (Exception e) {
                        log.error("Failed to authenticate WebSocket connection: {}", e.getMessage());
                    }
                }
            } else {
                log.warn("WebSocket connection attempt without Authorization header");
            }
        }
        
        return message;
    }
}

