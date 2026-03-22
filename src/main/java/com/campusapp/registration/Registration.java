package com.campusapp.registration;

import com.campusapp.auth.User;
import com.campusapp.common.BaseEntity;
import com.campusapp.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(nullable = false)
    private Double amountPaid = 0.0;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime paymentInitiatedAt;

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getPaymentInitiatedAt() { return paymentInitiatedAt; }
    public void setPaymentInitiatedAt(LocalDateTime paymentInitiatedAt) {
        this.paymentInitiatedAt = paymentInitiatedAt;
    }
}