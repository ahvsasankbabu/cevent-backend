package com.campusapp.dto;

import com.campusapp.event.EventCategory;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private EventCategory category;
    private LocalDate eventDate;
    private LocalTime startTime;
    private String venue;
    private Double fee;
    private Integer maxParticipants;
    private Integer registeredCount;
    private boolean active;
    private Long festId;
    private String festName;

    public EventResponse(Long id, String name, String description,
                         EventCategory category, LocalDate eventDate,
                         LocalTime startTime, String venue, Double fee,
                         Integer maxParticipants, Integer registeredCount,
                         boolean active, Long festId, String festName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.venue = venue;
        this.fee = fee;
        this.maxParticipants = maxParticipants;
        this.registeredCount = registeredCount;
        this.active = active;
        this.festId = festId;
        this.festName = festName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public EventCategory getCategory() { return category; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public String getVenue() { return venue; }
    public Double getFee() { return fee; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Integer getRegisteredCount() { return registeredCount; }
    public boolean isActive() { return active; }
    public Long getFestId() { return festId; }
    public String getFestName() { return festName; }
}