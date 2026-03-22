package com.campusapp.registration;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.EventRegistrationSummary;
import com.campusapp.dto.ParticipantResponse;
import com.campusapp.dto.RegistrationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerForEvent(
            @PathVariable Long eventId,
            Principal principal) {
        RegistrationResponse response = registrationService
                .registerForEvent(principal.getName(), eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Registered successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getMyRegistrations(
            Principal principal) {
        List<RegistrationResponse> registrations = registrationService
                .getMyRegistrations(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Registrations fetched", registrations));
    }

    @PutMapping("/{registrationId}/cancel")
    public ResponseEntity<ApiResponse<RegistrationResponse>> cancelRegistration(
            @PathVariable Long registrationId,
            Principal principal) {
        RegistrationResponse response = registrationService
                .cancelRegistration(principal.getName(), registrationId);
        return ResponseEntity.ok(
                ApiResponse.success("Registration cancelled", response));
    }

    @GetMapping("/college/fest/{festId}/summary")
    public ResponseEntity<ApiResponse<List<EventRegistrationSummary>>> getEventSummary(
            @PathVariable Long festId) {
        List<EventRegistrationSummary> summary = registrationService
                .getEventSummaryForCollege(festId);
        return ResponseEntity.ok(
                ApiResponse.success("Event summary fetched", summary));
    }

    // NEW — Get participants for a specific event (College side)
    @GetMapping("/event/{eventId}/participants")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getEventParticipants(
            @PathVariable Long eventId,
            Principal principal) {
        List<ParticipantResponse> participants = registrationService
                .getEventParticipants(principal.getName(), eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Participants fetched", participants));
    }

    // NEW — Get my registration status for a specific event (Student side)
    @GetMapping("/event/{eventId}/my-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyEventStatus(
            @PathVariable Long eventId,
            Principal principal) {
        Map<String, Object> status = registrationService
                .getMyEventStatus(principal.getName(), eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Status fetched", status));
    }
}