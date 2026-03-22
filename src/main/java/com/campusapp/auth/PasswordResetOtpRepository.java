package com.campusapp.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetOtp p WHERE p.email = :email")
    void deleteAllByEmail(String email);
}