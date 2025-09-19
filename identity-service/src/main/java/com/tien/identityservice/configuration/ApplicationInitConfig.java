package com.tien.identityservice.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// Seed dữ liệu khởi tạo khi ứng dụng chạy lần đầu: tạo các Role mặc định và User admin.

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin";

    // Bean này chỉ được tạo khi ứng dụng dùng MySQL driver tương ứng.
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            log.info("[INIT] Starting default data initialization...");

            // Bỏ qua nếu tài khoản admin đã tồn tại
            boolean adminExisted = userRepository.findByUsername(ADMIN_USERNAME).isPresent();
            if (adminExisted) {
                log.info("[INIT] User '{}' already exists. Skipping default seeding.", ADMIN_USERNAME);
                return;
            }

            // Tạo ROLE_USER
            Role userRole = roleRepository.save(Role.builder()
                    .name(PredefinedRole.USER_ROLE)
                    .description("User role")
                    .build());

            // Tạo ROLE_ADMIN
            Role adminRole = roleRepository.save(Role.builder()
                    .name(PredefinedRole.ADMIN_ROLE)
                    .description("Admin role")
                    .build());

            // Gán quyền cho tài khoản admin
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            // roles.add(userRole); // mở dòng này nếu muốn tài khoản admin có cả quyền USER

            User admin = User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .roles(roles)
                    .build();
            userRepository.save(admin);

            // Tạo tài khoản admin với mật khẩu đã được mã hoá
            log.warn(
                    "[INIT] Admin user '{}' created with default password '{}'. Please change it immediately.",
                    ADMIN_USERNAME,
                    ADMIN_PASSWORD);
            log.info("[INIT] Default data initialization completed.");
        };
    }
}
