package com.campusapp.dto;

public class CertificateTemplateRequest {

    private Long festId;
    private Long eventId;
    private String scope;

    public Long getFestId() { return festId; }
    public void setFestId(Long festId) { this.festId = festId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}