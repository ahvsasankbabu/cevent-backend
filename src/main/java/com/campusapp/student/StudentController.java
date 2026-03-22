package com.campusapp.student;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.StudentProfileRequest;
import com.campusapp.dto.StudentProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> createProfile(
            @Valid @RequestBody StudentProfileRequest request,
            Principal principal) {
        StudentProfileResponse response = studentService
                .createProfile(principal.getName(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Profile created successfully", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(
            Principal principal) {
        StudentProfileResponse response = studentService
                .getProfile(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
            @RequestBody StudentProfileRequest request,
            Principal principal) {
        StudentProfileResponse response = studentService
                .updateProfile(principal.getName(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", response));
    }
}