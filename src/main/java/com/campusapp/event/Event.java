package com.campusapp.event;

import com.campusapp.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "fest_id", nullable = false)
    private Fest fest;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private Double fee;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer registeredCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    public Fest getFest() { return fest; }
    public void setFest(Fest fest) { this.fest = fest; }

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

    public Integer getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(Integer registeredCount) { this.registeredCount = registeredCount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}