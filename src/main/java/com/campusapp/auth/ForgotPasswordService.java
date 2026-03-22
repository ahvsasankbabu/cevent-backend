package com.campusapp.auth;

import com.campusapp.email.EmailService;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public ForgotPasswordService(UserRepository userRepository,
                                  PasswordResetOtpRepository otpRepository,
                                  EmailService emailService,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // STEP 1 — User enters email, OTP sent
    public String sendOtp(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        otpRepository.deleteAllByEmail(email);

        String otp = generateOtp();

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtp(otp);
        resetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otpRepository.save(resetOtp);

        emailService.sendOtpEmail(email, otp);

        return "OTP sent to your email";
    }

    // STEP 2 — User enters OTP, verified
    public String verifyOtp(String email, String otp) {
        PasswordResetOtp resetOtp = otpRepository
                .findByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("No active OTP found. Please request a new one"));

        if (resetOtp.getAttempts() >= 3) {
            throw new BadRequestException("Maximum attempts exceeded. Please request a new OTP");
        }

        if (LocalDateTime.now().isAfter(resetOtp.getExpiresAt())) {
            throw new BadRequestException("OTP has expired. Please request a new one");
        }

        if (!resetOtp.getOtp().equals(otp)) {
            resetOtp.setAttempts(resetOtp.getAttempts() + 1);
            otpRepository.save(resetOtp);
            int remaining = 3 - resetOtp.getAttempts();
            throw new BadRequestException("Invalid OTP. " + remaining + " attempts remaining");
        }

        resetOtp.setVerified(true);
        otpRepository.save(resetOtp);

        return "OTP verified. Please reset your password";
    }

    // STEP 3 — User enters new password + confirm password
    public String resetPassword(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }

        if (newPassword.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        PasswordResetOtp resetOtp = otpRepository
                .findByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("Session expired. Please start over"));

        if (!resetOtp.isVerified()) {
            throw new BadRequestException("Please verify your OTP first");
        }

        if (LocalDateTime.now().isAfter(resetOtp.getExpiresAt().plusMinutes(5))) {
            throw new BadRequestException("Session expired. Please start over");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);
        otpRepository.deleteAllByEmail(email);

        return "Password reset successfully. Please login with your new password";
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}