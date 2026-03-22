package com.campusapp.payment;

import com.campusapp.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByOrderId(String orderId);

    Optional<PaymentOrder> findFirstByRegistrationAndStatusOrderByCreatedAtDesc(
            Registration registration, PaymentStatus status);

    List<PaymentOrder> findByStatusAndOrderExpiresAtBefore(
            PaymentStatus status, LocalDateTime dateTime);

    long countByRegistrationAndStatus(
            Registration registration, PaymentStatus status);
}