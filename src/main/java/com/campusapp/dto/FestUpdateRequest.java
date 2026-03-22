package com.campusapp.dto;

import java.time.LocalDate;

public class FestUpdateRequest {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private String city;
    private Double startingFee;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getStartingFee() { return startingFee; }
    public void setStartingFee(Double startingFee) { this.startingFee = startingFee; }
}