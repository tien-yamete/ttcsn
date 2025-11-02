package com.tien.identityservice.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.tien.identityservice.constant.SignInProvider;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, nullable = false)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    private SignInProvider provider;

    @Builder.Default
    Boolean isActive = false;

    @Column(length = 128)
    String providerUserId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist void prePersist() {
        if (email != null) email = email.trim().toLowerCase();
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate void preUpdate() {
        if (email != null) email = email.trim().toLowerCase();
        updatedAt = LocalDateTime.now();
    }

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Role> roles;
}
