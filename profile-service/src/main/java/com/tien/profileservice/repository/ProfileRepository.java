package com.tien.profileservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tien.profileservice.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);

    List<Profile> findByUsernameContainingIgnoreCase(String username);

    @Query("SELECT p FROM Profile p WHERE " +
           "LOWER(p.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> searchByKeyword(@Param("keyword") String keyword);
}
