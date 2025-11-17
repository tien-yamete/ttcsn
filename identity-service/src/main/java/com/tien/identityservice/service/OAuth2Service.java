package com.tien.identityservice.service;

import java.util.HashSet;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.constant.SignInProvider;
import com.tien.identityservice.dto.request.ProfileCreationRequest;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// OAuth2Service: Service xử lý đăng nhập qua OAuth2 (Google).
// Todo: Hỗ trợ nhiều providers: Facebook, GitHub, LinkedIn, etc.
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2Service {
    UserRepository userRepository;

    RoleRepository roleRepository;

    ProfileService profileService;

    @Transactional
    public User getOrCreateUserFromOAuth(String email, String name, String providerUserId, SignInProvider provider) {
        // Check if user already exists by email
        var existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update provider info if not set or different provider
            if (user.getProvider() == null || !user.getProvider().equals(provider)) {
                user.setProvider(provider);
                user.setProviderUserId(providerUserId);
                user.setEmailVerified(true);
                user.setIsActive(true);
                user = userRepository.save(user);
            }
            return user;
        }

        // Create new user from OAuth2 provider
        String[] nameParts = name != null && !name.isEmpty() ? name.split(" ", 2) : new String[] {"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Generate unique username
        String username = generateUniqueUsername(email);

        User user = User.builder()
                .email(email)
                .username(username)
                .provider(provider)
                .providerUserId(providerUserId)
                .emailVerified(true)
                .isActive(true)
                .build();

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        user = userRepository.save(user);

        // Create profile for the user
        ProfileCreationRequest profileRequest = ProfileCreationRequest.builder()
                .userId(user.getId())
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        profileService.createProfile(profileRequest);

        return user;
    }

    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername + "_" + System.currentTimeMillis();

        // Kiểm tra xem username đã tồn tại chưa
        int attempts = 0;
        while (userRepository.existsByUsername(username) && attempts < 5) {
            username = baseUsername + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(10000);
            attempts++;
        }

        return username;
    }
}
