package com.campusapp.payment;

import com.campusapp.common.BaseEntity;
import com.campusapp.registration.Registration;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentOrder extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "registration_id", nullable = false)
	private Registration registration;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime orderExpiresAt;

    @Column
    private Long remainingSeconds;

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) { this.registration = registration; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getOrderExpiresAt() { return orderExpiresAt; }
    public void setOrderExpiresAt(LocalDateTime orderExpiresAt) {
        this.orderExpiresAt = orderExpiresAt;
    }

    public Long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}