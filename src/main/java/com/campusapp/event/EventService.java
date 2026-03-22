package com.campusapp.event;

import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.college.College;
import com.campusapp.college.CollegeRepository;
import com.campusapp.college.CollegeStatus;
import com.campusapp.dto.*;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.exception.UnauthorizedException;
import com.campusapp.registration.Registration;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final FestRepository festRepository;
    private final EventRepository eventRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(FestRepository festRepository,
                        EventRepository eventRepository,
                        CollegeRepository collegeRepository,
                        UserRepository userRepository,
                        RegistrationRepository registrationRepository) {
        this.festRepository = festRepository;
        this.eventRepository = eventRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    public FestResponse createFest(String email, FestRequest request) {
        College college = getApprovedCollege(email);

        Fest fest = new Fest();
        fest.setCollege(college);
        fest.setName(request.getName());
        fest.setDescription(request.getDescription());
        fest.setStartDate(request.getStartDate());
        fest.setEndDate(request.getEndDate());
        fest.setVenue(request.getVenue());
        fest.setCity(request.getCity());
        fest.setStartingFee(request.getStartingFee());

        festRepository.save(fest);
        return mapFestToResponse(fest);
    }

    public EventResponse createEvent(String email, Long festId,
                                     EventRequest request) {
        College college = getApprovedCollege(email);

        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        if (!fest.getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only add events to your own fest");
        }

        Event event = new Event();
        event.setFest(fest);
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setVenue(request.getVenue());
        event.setFee(request.getFee());
        event.setMaxParticipants(request.getMaxParticipants());

        eventRepository.save(event);
        return mapEventToResponse(event);
    }

    // NEW — Edit event (partial update)
    @Transactional
    public EventResponse updateEvent(String email, Long eventId,
                                     EventUpdateRequest request) {
        College college = getApprovedCollege(email);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getFest().getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only edit your own events");
        }

        // Only update non-null fields
        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getStartTime() != null) event.setStartTime(request.getStartTime());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getFee() != null) event.setFee(request.getFee());
        if (request.getMaxParticipants() != null) {
            // Cannot reduce max below current registered count
            if (request.getMaxParticipants() < event.getRegisteredCount()) {
                throw new BadRequestException(
                        "Cannot reduce max participants below current registered count of "
                        + event.getRegisteredCount());
            }
            event.setMaxParticipants(request.getMaxParticipants());
        }

        eventRepository.save(event);
        return mapEventToResponse(event);
    }

    // NEW — Edit fest (partial update)
    @Transactional
    public FestResponse updateFest(String email, Long festId,
                                   FestUpdateRequest request) {
        College college = getApprovedCollege(email);

        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        if (!fest.getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only edit your own fests");
        }

        // Only update non-null fields
        if (request.getName() != null) fest.setName(request.getName());
        if (request.getDescription() != null) fest.setDescription(request.getDescription());
        if (request.getStartDate() != null) fest.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) fest.setEndDate(request.getEndDate());
        if (request.getVenue() != null) fest.setVenue(request.getVenue());
        if (request.getCity() != null) fest.setCity(request.getCity());
        if (request.getStartingFee() != null) fest.setStartingFee(request.getStartingFee());

        festRepository.save(fest);
        return mapFestToResponse(fest);
    }

    // NEW — Activate event
    @Transactional
    public EventResponse activateEvent(String email, Long eventId) {
        College college = getApprovedCollege(email);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getFest().getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only activate your own events");
        }

        event.setActive(true);
        eventRepository.save(event);
        return mapEventToResponse(event);
    }

    // NEW — Deactivate event
    @Transactional
    public EventResponse deactivateEvent(String email, Long eventId) {
        College college = getApprovedCollege(email);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getFest().getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only deactivate your own events");
        }

        event.setActive(false);

        // Cancel all PENDING registrations for this event
        List<Registration> pendingRegistrations = registrationRepository
                .findByEventAndStatus(event, RegistrationStatus.PENDING);

        for (Registration registration : pendingRegistrations) {
            registration.setStatus(RegistrationStatus.CANCELLED);
            registrationRepository.save(registration);
            event.setRegisteredCount(
                    Math.max(0, event.getRegisteredCount() - 1));
        }

        eventRepository.save(event);
        return mapEventToResponse(event);
    }

    // NEW — Activate fest
    @Transactional
    public FestResponse activateFest(String email, Long festId) {
        College college = getApprovedCollege(email);

        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        if (!fest.getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only activate your own fests");
        }

        fest.setActive(true);
        festRepository.save(fest);
        return mapFestToResponse(fest);
    }

    // NEW — Deactivate fest
    @Transactional
    public FestResponse deactivateFest(String email, Long festId) {
        College college = getApprovedCollege(email);

        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        if (!fest.getCollege().getId().equals(college.getId())) {
            throw new UnauthorizedException("You can only deactivate your own fests");
        }

        fest.setActive(false);

        // Cancel all PENDING registrations for all events in this fest
        List<Event> events = eventRepository.findByFest(fest);
        for (Event event : events) {
            List<Registration> pendingRegistrations = registrationRepository
                    .findByEventAndStatus(event, RegistrationStatus.PENDING);
            for (Registration registration : pendingRegistrations) {
                registration.setStatus(RegistrationStatus.CANCELLED);
                registrationRepository.save(registration);
                event.setRegisteredCount(
                        Math.max(0, event.getRegisteredCount() - 1));
            }
            eventRepository.save(event);
        }

        festRepository.save(fest);
        return mapFestToResponse(fest);
    }

    public List<FestResponse> getAllActiveFests() {
        return festRepository.findByActiveTrue()
                .stream()
                .map(this::mapFestToResponse)
                .collect(Collectors.toList());
    }

    public List<FestResponse> getMyFests(String email) {
        College college = getApprovedCollege(email);
        return festRepository.findByCollege(college)
                .stream()
                .map(this::mapFestToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByFest(Long festId) {
    	  return eventRepository.findByFestId(festId)
    	    .stream()
    	    .map(this::mapEventToResponse)
    	    .collect(Collectors.toList());
    	}


    public List<EventResponse> getEventsByCategory(Long festId,
                                                    EventCategory category) {
        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));
        return eventRepository.findByFestAndCategory(fest, category)
                .stream()
                .map(this::mapEventToResponse)
                .collect(Collectors.toList());
    }

    private College getApprovedCollege(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found"));

        if (college.getStatus() != CollegeStatus.APPROVED) {
            throw new UnauthorizedException("College not approved yet");
        }

        return college;
    }

    private FestResponse mapFestToResponse(Fest fest) {
        int totalEvents = eventRepository.findByFest(fest).size();
        return new FestResponse(
                fest.getId(),
                fest.getName(),
                fest.getDescription(),
                fest.getStartDate(),
                fest.getEndDate(),
                fest.getVenue(),
                fest.getCity(),
                fest.getStartingFee(),
                fest.getCollege().getCollegeName(),
                fest.isActive(),
                totalEvents
        );
    }

    private EventResponse mapEventToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getCategory(),
                event.getEventDate(),
                event.getStartTime(),
                event.getVenue(),
                event.getFee(),
                event.getMaxParticipants(),
                event.getRegisteredCount(),
                event.isActive(),
                event.getFest().getId(),
                event.getFest().getName()
        );
    }
}