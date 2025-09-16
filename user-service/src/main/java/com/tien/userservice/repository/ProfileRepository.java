package com.tien.userservice.repository;

import com.tien.userservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);

    List<Profile> findByUsernameContainingIgnoreCase(String username);
}
