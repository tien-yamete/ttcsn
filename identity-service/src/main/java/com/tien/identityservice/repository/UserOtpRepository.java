package com.tien.identityservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tien.identityservice.constant.OtpType;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.entity.UserOtp;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, String> {
    Optional<UserOtp> findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(User user, OtpType type);

    @Modifying
    @Query("UPDATE UserOtp u SET u.used = true WHERE u.user.id = :userId AND u.type = :type AND u.used = false")
    void deactivateOldOtp(@Param("userId") String userId, @Param("type") OtpType type);
}
