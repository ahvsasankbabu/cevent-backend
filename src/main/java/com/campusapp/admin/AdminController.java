package com.campusapp.admin;

import com.campusapp.auth.User;
import com.campusapp.common.ApiResponse;
import com.campusapp.dto.AdminDashboardStats;
import com.campusapp.dto.CollegeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/colleges")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllColleges() {
        List<CollegeResponse> colleges = adminService.getAllColleges();
        return ResponseEntity.ok(
                ApiResponse.success("All colleges fetched", colleges));
    }

    @GetMapping("/colleges/pending")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getPendingColleges() {
        List<CollegeResponse> colleges = adminService.getPendingColleges();
        return ResponseEntity.ok(
                ApiResponse.success("Pending colleges fetched", colleges));
    }

    @PutMapping("/colleges/{id}/approve")
    public ResponseEntity<ApiResponse<CollegeResponse>> approveCollege(
            @PathVariable Long id) {
        CollegeResponse college = adminService.approveCollege(id);
        return ResponseEntity.ok(
                ApiResponse.success("College approved successfully", college));
    }

    @PutMapping("/colleges/{id}/reject")
    public ResponseEntity<ApiResponse<CollegeResponse>> rejectCollege(
            @PathVariable Long id) {
        CollegeResponse college = adminService.rejectCollege(id);
        return ResponseEntity.ok(
                ApiResponse.success("College rejected", college));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success("All users fetched", users));
    }

    // NEW — Admin dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboardStats() {
        AdminDashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(
                ApiResponse.success("Admin stats fetched", stats));
    }
}