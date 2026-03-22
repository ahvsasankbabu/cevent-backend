package com.campusapp.dto;

import com.campusapp.event.EventCategory;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventUpdateRequest {

    private String name;
    private String description;
    private EventCategory category;
    private LocalDate eventDate;
    private LocalTime startTime;
    private String venue;
    private Double fee;
    private Integer maxParticipants;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EventCategory getCategory() { return category; }
    public void setCategory(EventCategory category) { this.category = category; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
}