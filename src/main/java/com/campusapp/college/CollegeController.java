package com.campusapp.college;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.CollegeDashboardStats;
import com.campusapp.dto.CollegeRegisterRequest;
import com.campusapp.dto.CollegeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/college")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CollegeResponse>> registerCollege(
            @Valid @RequestBody CollegeRegisterRequest request,
            Principal principal) {
        CollegeResponse response = collegeService.registerCollege(
                principal.getName(), request);
        return ResponseEntity.ok(
                ApiResponse.success("College registered successfully", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CollegeResponse>> getMyCollege(
            Principal principal) {
        CollegeResponse response = collegeService.getMyCollege(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("College profile fetched", response));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllApprovedColleges() {
        List<CollegeResponse> colleges = collegeService.getAllApprovedColleges();
        return ResponseEntity.ok(
                ApiResponse.success("Approved colleges fetched", colleges));
    }

    // NEW — College dashboard stats
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<CollegeDashboardStats>> getDashboardStats(
            Principal principal) {
        CollegeDashboardStats stats = collegeService
                .getDashboardStats(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Dashboard stats fetched", stats));
    }
}