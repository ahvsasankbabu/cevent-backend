package com.campusapp.search;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.EventResponse;
import com.campusapp.dto.FestResponse;
import com.campusapp.event.EventCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/fests")
    public ResponseEntity<ApiResponse<List<FestResponse>>> searchFests(
            @RequestParam String query) {
        List<FestResponse> results = searchService.searchFests(query);
        return ResponseEntity.ok(
                ApiResponse.success("Search results fetched", results));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> searchEvents(
            @RequestParam String query) {
        List<EventResponse> results = searchService.searchEvents(query);
        return ResponseEntity.ok(
                ApiResponse.success("Search results fetched", results));
    }

    @GetMapping("/fests/city")
    public ResponseEntity<ApiResponse<List<FestResponse>>> searchByCity(
            @RequestParam String city) {
        List<FestResponse> results = searchService.searchFestsByCity(city);
        return ResponseEntity.ok(
                ApiResponse.success("Fests by city fetched", results));
    }

    @GetMapping("/events/category")
    public ResponseEntity<ApiResponse<List<EventResponse>>> searchByCategory(
            @RequestParam EventCategory category) {
        List<EventResponse> results = searchService
                .searchEventsByCategory(category);
        return ResponseEntity.ok(
                ApiResponse.success("Events by category fetched", results));
    }
}