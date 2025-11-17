package com.tien.identityservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.identityservice.constant.OtpType;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.entity.UserOtp;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.repository.UserOtpRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// OtpService: Service xử lý OTP (One-Time Password) cho xác thực email.

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService {
    UserOtpRepository userOtpRepository;

    /// Tạo mã OTP 6 chữ số ngẫu nhiên
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    // Tạo và lưu OTP vào database với thời gian hết hạn
    @Transactional
    public UserOtp createOtp(User user, OtpType type, int expiryMinutes) {
        String otpCode = generateVerificationCode();

        UserOtp userOtp = UserOtp.builder()
                .user(user)
                .otpCode(otpCode)
                .type(type)
                .expiryTime(LocalDateTime.now().plusMinutes(expiryMinutes))
                .used(false)
                .build();

        return userOtpRepository.save(userOtp);
    }

    // Tìm OTP mới nhất chưa được sử dụng của user
    public UserOtp findLatestOtp(User user, OtpType type) {
        return userOtpRepository
                .findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, type)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
    }

    // Kiểm tra OTP có hợp lệ không (đúng mã, chưa hết hạn)
    @Transactional
    public void validateOtp(UserOtp userOtp, String providedOtp) {
        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!userOtp.getOtpCode().equals(providedOtp)) {
            userOtp.setUsed(true); // Đánh dấu OTP sai là đã dùng
            userOtpRepository.save(userOtp);
            throw new AppException(ErrorCode.OTP_INVALID);
        }
    }

    // Đánh dấu OTP đã được sử dụng
    @Transactional
    public void markOtpAsUsed(UserOtp userOtp) {
        userOtp.setUsed(true);
        userOtpRepository.save(userOtp);
    }

    // Kiểm tra tần suất gửi OTP (tránh spam, tối thiểu 60 giây)
    public void checkOtpFrequency(User user, OtpType type) {
        Optional<UserOtp> lastOtp = userOtpRepository.findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, type);
        if (lastOtp.isPresent()
                && lastOtp.get().getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
            throw new AppException(ErrorCode.OTP_TOO_FREQUENT);
        }
    }

    // Vô hiệu hóa các OTP cũ của user
    @Transactional
    public void deactivateOldOtps(String userId, OtpType type) {
        userOtpRepository.deactivateOldOtp(userId, type);
    }
}
