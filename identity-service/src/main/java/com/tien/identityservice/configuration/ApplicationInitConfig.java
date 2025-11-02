package com.tien.identityservice.configuration;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.constant.SignInProvider;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {

    final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.username}")
    protected String adminUsername;

    @Value("${app.seed.admin.password}")
    protected String adminPassword;

    @Value("${app.seed.admin.email}")
    protected String adminEmail;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            log.info("[INIT] Starting default data initialization...");

            // 1) Ensure roles
            Role userRole = roleRepository.findByName(PredefinedRole.USER_ROLE)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(PredefinedRole.USER_ROLE)
                            .description("User role")
                            .build()));

            Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_ROLE)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(PredefinedRole.ADMIN_ROLE)
                            .description("Admin role")
                            .build()));

            // 2) Ensure admin user
            boolean adminExisted = userRepository.findByUsername(adminUsername).isPresent()
                    || userRepository.findByEmail(adminEmail).isPresent();
            if (adminExisted) {
                log.info("[INIT] Admin '{}' or email '{}' already exists. Skipping seed.", adminUsername, adminEmail);
                return;
            }

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            // roles.add(userRole); // mở nếu muốn admin có cả USER

            LocalDateTime now = LocalDateTime.now();

            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .emailVerified(true)
                    .isActive(true)
                    .provider(SignInProvider.LOCAL)
                    .roles(roles)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userRepository.save(admin);

            log.warn("[INIT] Admin '{}' created with default password '{}'. Please change it ASAP.",
                    adminUsername, adminPassword);
            log.info("[INIT] Default data initialization completed.");
        };
    }
}
