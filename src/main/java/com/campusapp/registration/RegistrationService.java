package com.campusapp.registration;

import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.dto.EventRegistrationSummary;
import com.campusapp.dto.ParticipantResponse;
import com.campusapp.dto.RegistrationResponse;
import com.campusapp.event.Event;
import com.campusapp.event.EventRepository;
import com.campusapp.event.Fest;
import com.campusapp.event.FestRepository;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.exception.UnauthorizedException;
import com.campusapp.student.StudentProfile;
import com.campusapp.student.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FestRepository festRepository;
    private final StudentProfileRepository studentProfileRepository;

    private static final int PAYMENT_TIMEOUT_MINUTES = 30;

    public RegistrationService(RegistrationRepository registrationRepository,
                                UserRepository userRepository,
                                EventRepository eventRepository,
                                FestRepository festRepository,
                                StudentProfileRepository studentProfileRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.festRepository = festRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public RegistrationResponse registerForEvent(String email, Long eventId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!student.getRole().name().equals("STUDENT")) {
            throw new UnauthorizedException("Only students can register for events");
        }

        // Pessimistic lock — prevents race condition
        Event event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.isActive()) {
            throw new BadRequestException("Event is not active");
        }

        // Fixed — only blocks PENDING or CONFIRMED, not CANCELLED
        if (registrationRepository.existsActiveRegistration(student, event)) {
            throw new BadRequestException("You already have an active registration for this event");
        }

        if (event.getRegisteredCount() >= event.getMaxParticipants()) {
            throw new BadRequestException("Event is full");
        }

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setEvent(event);

        // Free event — auto confirm immediately
        if (event.getFee() == 0.0) {
            registration.setStatus(RegistrationStatus.CONFIRMED);
            registration.setAmountPaid(0.0);
        } else {
            // Paid event — set expiry for 30 min timeout
            registration.setStatus(RegistrationStatus.PENDING);
            registration.setExpiresAt(
                    LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES));
        }

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);
        registrationRepository.save(registration);

        return mapToResponse(registration);
    }

    public List<RegistrationResponse> getMyRegistrations(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return registrationRepository.findByStudent(student)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RegistrationResponse cancelRegistration(String email,
                                                    Long registrationId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Registration registration = registrationRepository
                .findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found"));

        if (!registration.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not your registration");
        }

        if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
            throw new BadRequestException(
                    "Cannot cancel a confirmed registration. Payment was already made");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BadRequestException("Registration is already cancelled");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);

        Event event = registration.getEvent();
        event.setRegisteredCount(event.getRegisteredCount() - 1);
        eventRepository.save(event);

        registrationRepository.save(registration);
        return mapToResponse(registration);
    }

    public List<EventRegistrationSummary> getEventSummaryForCollege(
            Long festId) {
        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        List<Event> events = eventRepository.findByFestAndActiveTrue(fest);

        return events.stream().map(event -> {
            List<Registration> registrations = registrationRepository
                    .findByEvent(event);
            int confirmed = registrationRepository.countByEventAndStatus(
                    event, RegistrationStatus.CONFIRMED);
            double totalAmount = registrations.stream()
                    .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                    .mapToDouble(Registration::getAmountPaid)
                    .sum();
            return new EventRegistrationSummary(
                    event.getId(),
                    event.getName(),
                    registrations.size(),
                    confirmed,
                    totalAmount
            );
        }).collect(Collectors.toList());
    }

    // NEW — Get participants for a specific event (College side)
    public List<ParticipantResponse> getEventParticipants(String email,
                                                           Long eventId) {
        // Verify college owns this event
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getFest().getCollege().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Not your event");
        }

        // Only return CONFIRMED participants
        return registrationRepository
                .findByEventAndStatus(event, RegistrationStatus.CONFIRMED)
                .stream()
                .map(registration -> {
                    User student = registration.getStudent();
                    StudentProfile profile = studentProfileRepository
                            .findByUser(student)
                            .orElse(null);

                    String rollNumber = profile != null ? profile.getRollNumber() : "N/A";
                    String branch = profile != null ? profile.getBranch() : "N/A";
                    String collegeName = profile != null ? profile.getCollegeName() : "N/A";

                    return new ParticipantResponse(
                            registration.getId(),
                            student.getName(),
                            student.getEmail(),
                            rollNumber,
                            branch,
                            collegeName,
                            registration.getCreatedAt(),
                            registration.getAmountPaid(),
                            registration.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }

    // NEW — Get my registration status for a specific event (Student side)
    public java.util.Map<String, Object> getMyEventStatus(String email,
                                                           Long eventId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        registrationRepository.findByStudentAndEvent(student, event)
                .ifPresentOrElse(
                        registration -> {
                            response.put("registered", true);
                            response.put("status", registration.getStatus().name());
                            response.put("registrationId", registration.getId());
                        },
                        () -> {
                            response.put("registered", false);
                            response.put("status", null);
                            response.put("registrationId", null);
                        }
                );

        return response;
    }

    private RegistrationResponse mapToResponse(Registration registration) {
        Event event = registration.getEvent();
        return new RegistrationResponse(
                registration.getId(),
                event.getName(),
                event.getFest().getName(),
                event.getFest().getCollege().getCollegeName(),
                event.getEventDate().toString(),
                event.getVenue(),
                event.getFee(),
                registration.getStatus(),
                registration.getAmountPaid(),
                registration.getCreatedAt()
        );
    }
}