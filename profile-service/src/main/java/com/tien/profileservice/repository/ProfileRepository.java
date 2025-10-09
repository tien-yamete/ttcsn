package com.tien.profileservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tien.profileservice.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);

    List<Profile> findByUsernameContainingIgnoreCase(String username);
}
