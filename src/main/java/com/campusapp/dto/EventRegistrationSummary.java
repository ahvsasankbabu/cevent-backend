package com.campusapp.dto;

public class EventRegistrationSummary {

    private Long eventId;
    private String eventName;
    private int totalRegistrations;
    private int confirmedRegistrations;
    private Double totalAmountCollected;

    public EventRegistrationSummary(Long eventId, String eventName,
                                     int totalRegistrations,
                                     int confirmedRegistrations,
                                     Double totalAmountCollected) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalRegistrations = totalRegistrations;
        this.confirmedRegistrations = confirmedRegistrations;
        this.totalAmountCollected = totalAmountCollected;
    }

    public Long getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public int getTotalRegistrations() { return totalRegistrations; }
    public int getConfirmedRegistrations() { return confirmedRegistrations; }
    public Double getTotalAmountCollected() { return totalAmountCollected; }
}