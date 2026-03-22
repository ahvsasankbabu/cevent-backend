package com.campusapp.payment;

import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.dto.PaymentOrderResponse;
import com.campusapp.dto.SlotStatusResponse;
import com.campusapp.event.Event;
import com.campusapp.event.EventRepository;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.exception.UnauthorizedException;
import com.campusapp.registration.Registration;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public PaymentService(PaymentOrderRepository paymentOrderRepository,
                          RegistrationRepository registrationRepository,
                          UserRepository userRepository,
                          EventRepository eventRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    // STEP 1 — Student initiates payment
    @Transactional
    public PaymentOrderResponse initiatePayment(String email, Long registrationId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        // Security check — must be their own registration
        if (!registration.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not your registration");
        }

        // Already paid check
        if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
            throw new BadRequestException("Already paid for this event");
        }

        // Cancelled check
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BadRequestException("Registration expired. Please register again");
        }

        // Check registration not expired
        if (registration.getExpiresAt() != null &&
                LocalDateTime.now().isAfter(registration.getExpiresAt())) {
            throw new BadRequestException("Registration expired. Please register again");
        }

        // Max retry limit — enterprise grade fraud prevention
        long failedAttempts = paymentOrderRepository
                .countByRegistrationAndStatus(registration, PaymentStatus.FAILED);
        if (failedAttempts >= 5) {
            throw new BadRequestException(
                    "Maximum payment attempts exceeded. Please contact support");
        }

        // Return existing active PENDING order if already exists
        PaymentOrder existingOrder = paymentOrderRepository
                .findFirstByRegistrationAndStatusOrderByCreatedAtDesc(
                        registration, PaymentStatus.PENDING)
                .orElse(null);

        if (existingOrder != null) {
            // Pause timer — student is in UPI app
            registration.setPaymentInitiatedAt(LocalDateTime.now());
            registrationRepository.save(registration);
            return mapToResponse(existingOrder);
        }

        // Create new payment order
        PaymentOrder order = new PaymentOrder();
        order.setRegistration(registration);
        order.setOrderId("ORD-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase());
        order.setAmount(registration.getEvent().getFee());
        order.setStatus(PaymentStatus.PENDING);
        order.setOrderExpiresAt(registration.getExpiresAt());

        // Pause timer — student is going to UPI app
        registration.setPaymentInitiatedAt(LocalDateTime.now());
        registrationRepository.save(registration);

        paymentOrderRepository.save(order);
        return mapToResponse(order);
    }

    // STEP 2 — Student confirms payment (simulated)
    @Transactional
    public PaymentOrderResponse confirmPayment(String email, String orderId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found"));

        // Security check
        if (!order.getRegistration().getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not your payment order");
        }

        // Already paid check
        if (order.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already confirmed");
        }

        // Expired check
        if (order.getStatus() == PaymentStatus.EXPIRED) {
            throw new BadRequestException("Payment order expired. Please register again");
        }

        // Check order not expired
        if (LocalDateTime.now().isAfter(order.getOrderExpiresAt())) {
            order.setStatus(PaymentStatus.EXPIRED);
            paymentOrderRepository.save(order);
            throw new BadRequestException("Payment order expired. Please register again");
        }

        // Confirm payment
        order.setStatus(PaymentStatus.SUCCESS);
        order.setPaidAt(LocalDateTime.now());
        paymentOrderRepository.save(order);

        // Update registration
        Registration registration = order.getRegistration();
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setAmountPaid(order.getAmount());
        registration.setPaymentInitiatedAt(null);
        registrationRepository.save(registration);

        return mapToResponse(order);
    }

    // STEP 3 — Student cancels payment (goes back from UPI app)
    @Transactional
    public PaymentOrderResponse cancelPayment(String email, String orderId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found"));

        // Security check
        if (!order.getRegistration().getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not your payment order");
        }

        if (order.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Cannot cancel a successful payment");
        }

        // Resume timer — calculate remaining time and update expiresAt
        Registration registration = order.getRegistration();

        if (registration.getPaymentInitiatedAt() != null) {
            long secondsSpentInUpiApp = ChronoUnit.SECONDS.between(
                    registration.getPaymentInitiatedAt(), LocalDateTime.now());
            LocalDateTime newExpiresAt = registration.getExpiresAt()
                    .plusSeconds(secondsSpentInUpiApp);
            registration.setExpiresAt(newExpiresAt);
            registration.setPaymentInitiatedAt(null);
            registrationRepository.save(registration);
        }

        // Mark order as failed
        order.setStatus(PaymentStatus.FAILED);
        paymentOrderRepository.save(order);

        return mapToResponse(order);
    }

    // Slot status for frontend polling
    public SlotStatusResponse getSlotStatus(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        int totalSlots = event.getMaxParticipants();
        int registeredCount = event.getRegisteredCount();
        int confirmedCount = registrationRepository.countByEventAndStatus(
                event, RegistrationStatus.CONFIRMED);
        int pendingCount = registrationRepository.countByEventAndStatus(
                event, RegistrationStatus.PENDING);
        int availableSlots = totalSlots - registeredCount;

        String slotStatus;

        if (!event.isActive()) {
            slotStatus = "FULL";
        } else if (availableSlots <= 0) {
            if (pendingCount > 0) {
                slotStatus = "PENDING_PAYMENT";
            } else {
                slotStatus = "FULL";
            }
        } else if (availableSlots <= 4) {
            slotStatus = "FILLING_FAST";
        } else {
            slotStatus = "AVAILABLE";
        }

        return new SlotStatusResponse(
                availableSlots, totalSlots, confirmedCount, pendingCount, slotStatus);
    }
 // NEW — Get payment order status
    public PaymentOrderResponse getOrderStatus(String email, String orderId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found"));

        // Security check
        if (!order.getRegistration().getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not your payment order");
        }

        return mapToResponse(order);
    }

    private PaymentOrderResponse mapToResponse(PaymentOrder order) {
        Registration registration = order.getRegistration();
        Event event = registration.getEvent();

        long remainingSeconds = 0;
        if (order.getOrderExpiresAt() != null) {
            remainingSeconds = ChronoUnit.SECONDS.between(
                    LocalDateTime.now(), order.getOrderExpiresAt());
            if (remainingSeconds < 0) remainingSeconds = 0;
        }

        return new PaymentOrderResponse(
                order.getOrderId(),
                order.getAmount(),
                order.getStatus(),
                order.getOrderExpiresAt(),
                remainingSeconds,
                event.getName(),
                event.getFest().getName(),
                registration.getId()
        );
    }
}