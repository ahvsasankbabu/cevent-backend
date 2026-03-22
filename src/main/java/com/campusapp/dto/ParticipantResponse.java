package com.campusapp.dto;

import com.campusapp.registration.RegistrationStatus;
import java.time.LocalDateTime;

public class ParticipantResponse {

    private Long registrationId;
    private String studentName;
    private String email;
    private String rollNumber;
    private String branch;
    private String collegeName;
    private LocalDateTime registeredAt;
    private Double amountPaid;
    private RegistrationStatus status;

    public ParticipantResponse(Long registrationId, String studentName,
                                String email, String rollNumber,
                                String branch, String collegeName,
                                LocalDateTime registeredAt, Double amountPaid,
                                RegistrationStatus status) {
        this.registrationId = registrationId;
        this.studentName = studentName;
        this.email = email;
        this.rollNumber = rollNumber;
        this.branch = branch;
        this.collegeName = collegeName;
        this.registeredAt = registeredAt;
        this.amountPaid = amountPaid;
        this.status = status;
    }

    public Long getRegistrationId() { return registrationId; }
    public String getStudentName() { return studentName; }
    public String getEmail() { return email; }
    public String getRollNumber() { return rollNumber; }
    public String getBranch() { return branch; }
    public String getCollegeName() { return collegeName; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public Double getAmountPaid() { return amountPaid; }
    public RegistrationStatus getStatus() { return status; }
}