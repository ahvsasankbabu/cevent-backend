package com.campusapp.search;

import com.campusapp.dto.EventResponse;
import com.campusapp.dto.FestResponse;
import com.campusapp.event.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final FestRepository festRepository;
    private final EventRepository eventRepository;

    public SearchService(FestRepository festRepository,
                         EventRepository eventRepository) {
        this.festRepository = festRepository;
        this.eventRepository = eventRepository;
    }

    public List<FestResponse> searchFests(String query) {
        String[] tokens = tokenize(query);

        return festRepository.findByActiveTrue()
                .stream()
                .map(fest -> {
                    int score = scoreFest(fest, tokens);
                    return new AbstractMap.SimpleEntry<>(fest, score);
                })
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(entry -> mapFestToResponse(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String query) {
        String[] tokens = tokenize(query);

        return eventRepository.findByActiveTrue()
                .stream()
                .map(event -> {
                    int score = scoreEvent(event, tokens);
                    return new AbstractMap.SimpleEntry<>(event, score);
                })
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(entry -> mapEventToResponse(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<FestResponse> searchFestsByCity(String city) {
        String[] tokens = tokenize(city);
        return festRepository.findByActiveTrue()
                .stream()
                .filter(fest -> matchesAnyToken(fest.getCity(), tokens) ||
                                matchesAnyToken(fest.getCollege().getCity(), tokens) ||
                                matchesAnyToken(fest.getCollege().getState(), tokens))
                .map(this::mapFestToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> searchEventsByCategory(EventCategory category) {
        return eventRepository.findByActiveTrue()
                .stream()
                .filter(event -> event.getCategory() == category)
                .map(this::mapEventToResponse)
                .collect(Collectors.toList());
    }

    private String[] tokenize(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new String[0];
        }
        StringTokenizer tokenizer = new StringTokenizer(query.trim(), " ,.-_/\\");
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken().toLowerCase());
        }
        return tokens.toArray(new String[0]);
    }

    private int scoreFest(Fest fest, String[] tokens) {
        int score = 0;
        for (String token : tokens) {
            if (matchesToken(fest.getName(), token)) score += 3;
            if (matchesToken(fest.getCity(), token)) score += 2;
            if (matchesToken(fest.getVenue(), token)) score += 2;
            if (matchesToken(fest.getDescription(), token)) score += 1;
            if (matchesToken(fest.getCollege().getCollegeName(), token)) score += 2;
            if (matchesToken(fest.getCollege().getCity(), token)) score += 2;
            if (matchesToken(fest.getCollege().getState(), token)) score += 1;
            if (matchesToken(fest.getCollege().getAddress(), token)) score += 1;
        }
        return score;
    }

    private int scoreEvent(Event event, String[] tokens) {
        int score = 0;
        for (String token : tokens) {
            if (matchesToken(event.getName(), token)) score += 3;
            if (matchesToken(event.getDescription(), token)) score += 1;
            if (matchesToken(event.getVenue(), token)) score += 2;
            if (matchesToken(event.getCategory().name(), token)) score += 2;
            if (matchesToken(event.getFest().getName(), token)) score += 2;
            if (matchesToken(event.getFest().getCity(), token)) score += 1;
            if (matchesToken(
                    event.getFest().getCollege().getCollegeName(), token)) score += 2;
        }
        return score;
    }

    private boolean matchesToken(String field, String token) {
        if (field == null) return false;
        return field.toLowerCase().contains(token);
    }

    private boolean matchesAnyToken(String field, String[] tokens) {
        if (field == null) return false;
        for (String token : tokens) {
            if (field.toLowerCase().contains(token)) return true;
        }
        return false;
    }

    private FestResponse mapFestToResponse(Fest fest) {
        int totalEvents = eventRepository.findByFestAndActiveTrue(fest).size();
        return new FestResponse(
                fest.getId(),
                fest.getName(),
                fest.getDescription(),
                fest.getStartDate(),
                fest.getEndDate(),
                fest.getVenue(),
                fest.getCity(),
                fest.getStartingFee(),
                fest.getCollege().getCollegeName(),
                fest.isActive(),
                totalEvents
        );
    }

    private EventResponse mapEventToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getCategory(),
                event.getEventDate(),
                event.getStartTime(),
                event.getVenue(),
                event.getFee(),
                event.getMaxParticipants(),
                event.getRegisteredCount(),
                event.isActive(),
                event.getFest().getId(),
                event.getFest().getName()
        );
    }
}