package com.campusapp.dto;

import java.time.LocalDate;

public class FestResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private String city;
    private Double startingFee;
    private String collegeName;
    private boolean active;
    private int totalEvents;

    public FestResponse(Long id, String name, String description,
            LocalDate startDate, LocalDate endDate,
            String venue, String city, Double startingFee,
            String collegeName, boolean active, int totalEvents) {
    			this.id = id;
    			this.name = name;
    			this.description = description;
    			this.startDate = startDate;
    			this.endDate = endDate;
    			this.venue = venue;
    			this.city = city;
    			this.startingFee = startingFee;
    			this.collegeName = collegeName;
    			this.active = active;
    			this.totalEvents = totalEvents;
    }
    public int getTotalEvents() { return totalEvents; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getVenue() { return venue; }
    public String getCity() { return city; }
    public Double getStartingFee() { return startingFee; }
    public String getCollegeName() { return collegeName; }
    public boolean isActive() { return active; }
}