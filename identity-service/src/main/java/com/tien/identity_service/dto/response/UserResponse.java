package com.tien.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserResponse {
    String id;
    String username;
    String email;
    boolean emailVerified;
    Set<RoleResponse> roles;
}
