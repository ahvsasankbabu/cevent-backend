package com.campusapp.dto;

import com.campusapp.registration.RegistrationStatus;
import java.time.LocalDateTime;

public class RegistrationResponse {

    private Long id;
    private String eventName;
    private String festName;
    private String collegeName;
    private String eventDate;
    private String venue;
    private Double fee;
    private RegistrationStatus status;
    private Double amountPaid;
    private LocalDateTime registeredAt;

    public RegistrationResponse(Long id, String eventName, String festName,
                                 String collegeName, String eventDate,
                                 String venue, Double fee,
                                 RegistrationStatus status, Double amountPaid,
                                 LocalDateTime registeredAt) {
        this.id = id;
        this.eventName = eventName;
        this.festName = festName;
        this.collegeName = collegeName;
        this.eventDate = eventDate;
        this.venue = venue;
        this.fee = fee;
        this.status = status;
        this.amountPaid = amountPaid;
        this.registeredAt = registeredAt;
    }

    public Long getId() { return id; }
    public String getEventName() { return eventName; }
    public String getFestName() { return festName; }
    public String getCollegeName() { return collegeName; }
    public String getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public Double getFee() { return fee; }
    public RegistrationStatus getStatus() { return status; }
    public Double getAmountPaid() { return amountPaid; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}