package com.campusapp.dto;

import com.campusapp.payment.PaymentStatus;
import java.time.LocalDateTime;

public class PaymentOrderResponse {

    private String orderId;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime orderExpiresAt;
    private Long remainingSeconds;
    private String eventName;
    private String festName;
    private Long registrationId;

    public PaymentOrderResponse(String orderId, Double amount,
                                 PaymentStatus status,
                                 LocalDateTime orderExpiresAt,
                                 Long remainingSeconds,
                                 String eventName, String festName,
                                 Long registrationId) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.orderExpiresAt = orderExpiresAt;
        this.remainingSeconds = remainingSeconds;
        this.eventName = eventName;
        this.festName = festName;
        this.registrationId = registrationId;
    }

    public String getOrderId() { return orderId; }
    public Double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getOrderExpiresAt() { return orderExpiresAt; }
    public Long getRemainingSeconds() { return remainingSeconds; }
    public String getEventName() { return eventName; }
    public String getFestName() { return festName; }
    public Long getRegistrationId() { return registrationId; }
}