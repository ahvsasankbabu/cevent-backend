package com.campusapp.event;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/fest")
    public ResponseEntity<ApiResponse<FestResponse>> createFest(
            @Valid @RequestBody FestRequest request,
            Principal principal) {
        FestResponse response = eventService.createFest(
                principal.getName(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Fest created successfully", response));
    }

    @PostMapping("/fest/{festId}/event")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @PathVariable Long festId,
            @Valid @RequestBody EventRequest request,
            Principal principal) {
        EventResponse response = eventService.createEvent(
                principal.getName(), festId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Event created successfully", response));
    }

    @GetMapping("/fests")
    public ResponseEntity<ApiResponse<List<FestResponse>>> getAllFests() {
        List<FestResponse> fests = eventService.getAllActiveFests();
        return ResponseEntity.ok(
                ApiResponse.success("Fests fetched successfully", fests));
    }

    @GetMapping("/my-fests")
    public ResponseEntity<ApiResponse<List<FestResponse>>> getMyFests(
            Principal principal) {
        List<FestResponse> fests = eventService.getMyFests(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("My fests fetched", fests));
    }

    @GetMapping("/fest/{festId}")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByFest(
            @PathVariable Long festId) {
        List<EventResponse> events = eventService.getEventsByFest(festId);
        return ResponseEntity.ok(
                ApiResponse.success("Events fetched successfully", events));
    }

    @GetMapping("/fest/{festId}/category/{category}")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByCategory(
            @PathVariable Long festId,
            @PathVariable EventCategory category) {
        List<EventResponse> events = eventService.getEventsByCategory(
                festId, category);
        return ResponseEntity.ok(
                ApiResponse.success("Events fetched by category", events));
    }

    // NEW — Edit event
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request,
            Principal principal) {
        EventResponse response = eventService.updateEvent(
                principal.getName(), eventId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Event updated successfully", response));
    }

    // NEW — Edit fest
    @PutMapping("/fest/{festId}")
    public ResponseEntity<ApiResponse<FestResponse>> updateFest(
            @PathVariable Long festId,
            @RequestBody FestUpdateRequest request,
            Principal principal) {
        FestResponse response = eventService.updateFest(
                principal.getName(), festId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Fest updated successfully", response));
    }

    // NEW — Activate event
    @PutMapping("/{eventId}/activate")
    public ResponseEntity<ApiResponse<EventResponse>> activateEvent(
            @PathVariable Long eventId,
            Principal principal) {
        EventResponse response = eventService.activateEvent(
                principal.getName(), eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Event activated successfully", response));
    }

    // NEW — Deactivate event
    @PutMapping("/{eventId}/deactivate")
    public ResponseEntity<ApiResponse<EventResponse>> deactivateEvent(
            @PathVariable Long eventId,
            Principal principal) {
        EventResponse response = eventService.deactivateEvent(
                principal.getName(), eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Event deactivated successfully", response));
    }

    // NEW — Activate fest
    @PutMapping("/fest/{festId}/activate")
    public ResponseEntity<ApiResponse<FestResponse>> activateFest(
            @PathVariable Long festId,
            Principal principal) {
        FestResponse response = eventService.activateFest(
                principal.getName(), festId);
        return ResponseEntity.ok(
                ApiResponse.success("Fest activated successfully", response));
    }

    // NEW — Deactivate fest
    @PutMapping("/fest/{festId}/deactivate")
    public ResponseEntity<ApiResponse<FestResponse>> deactivateFest(
            @PathVariable Long festId,
            Principal principal) {
        FestResponse response = eventService.deactivateFest(
                principal.getName(), festId);
        return ResponseEntity.ok(
                ApiResponse.success("Fest deactivated successfully", response));
    }
}