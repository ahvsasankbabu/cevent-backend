package com.campusapp.payment;

import com.campusapp.event.Event;
import com.campusapp.event.EventRepository;
import com.campusapp.registration.Registration;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RegistrationScheduler {

    private static final Logger logger = LoggerFactory
            .getLogger(RegistrationScheduler.class);

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    public RegistrationScheduler(RegistrationRepository registrationRepository,
                                  EventRepository eventRepository,
                                  PaymentOrderRepository paymentOrderRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredRegistrations() {
        LocalDateTime now = LocalDateTime.now();

        List<Registration> expiredRegistrations = registrationRepository
                .findExpiredPendingRegistrations(now);

        if (expiredRegistrations.isEmpty()) {
            return;
        }

        logger.info("Found {} expired registrations to cancel",
                expiredRegistrations.size());

        for (Registration registration : expiredRegistrations) {
            try {
                // Skip if payment was initiated — timer is paused
                if (registration.getPaymentInitiatedAt() != null) {
                    logger.info("Skipping registration {} — payment in progress",
                            registration.getId());
                    continue;
                }

                // Cancel registration
                registration.setStatus(RegistrationStatus.CANCELLED);
                registrationRepository.save(registration);

                // Decrement registered count
                Event event = registration.getEvent();
                event.setRegisteredCount(
                        Math.max(0, event.getRegisteredCount() - 1));
                eventRepository.save(event);

                paymentOrderRepository
                .findFirstByRegistrationAndStatusOrderByCreatedAtDesc(
                        registration, PaymentStatus.PENDING)
                .ifPresent(order -> {
                    order.setStatus(PaymentStatus.EXPIRED);
                    paymentOrderRepository.save(order);
                });

                logger.info("Cancelled expired registration {} for event {}",
                        registration.getId(), event.getName());

            } catch (Exception e) {
                logger.error("Error cancelling registration {}: {}",
                        registration.getId(), e.getMessage());
            }
        }
    }
}