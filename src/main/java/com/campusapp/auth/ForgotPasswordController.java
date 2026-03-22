package com.campusapp.auth;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.ForgotPasswordRequest;
import com.campusapp.dto.ResetPasswordRequest;
import com.campusapp.dto.VerifyOtpRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        String message = forgotPasswordService.sendOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        String message = forgotPasswordService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        String message = forgotPasswordService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getConfirmPassword());
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}