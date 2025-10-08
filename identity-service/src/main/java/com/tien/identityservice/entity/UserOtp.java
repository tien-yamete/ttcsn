package com.tien.identityservice.entity;

import com.tien.identityservice.constant.OtpType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_otp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpType type; // REGISTER, RESET_PASSWORD

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean used = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.expiryTime == null) {
            this.expiryTime = LocalDateTime.now().plusMinutes(2);
        }
    }
}
