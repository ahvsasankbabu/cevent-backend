package com.campusapp.college;

import com.campusapp.auth.Role;
import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.dto.CollegeDashboardStats;
import com.campusapp.dto.CollegeRegisterRequest;
import com.campusapp.dto.CollegeResponse;
import com.campusapp.event.Event;
import com.campusapp.event.EventRepository;
import com.campusapp.event.Fest;
import com.campusapp.event.FestRepository;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.exception.UnauthorizedException;
import com.campusapp.registration.Registration;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final FestRepository festRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public CollegeService(CollegeRepository collegeRepository,
                          UserRepository userRepository,
                          FestRepository festRepository,
                          EventRepository eventRepository,
                          RegistrationRepository registrationRepository) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.festRepository = festRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public CollegeResponse registerCollege(String email,
                                           CollegeRegisterRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (collegeRepository.existsByUser(user)) {
            throw new BadRequestException("College profile already exists");
        }

        if (collegeRepository.existsByCollegeEmail(request.getCollegeEmail())) {
            throw new BadRequestException("College email already registered");
        }

        College college = new College();
        college.setUser(user);
        college.setCollegeName(request.getCollegeName());
        college.setCity(request.getCity());
        college.setState(request.getState());
        college.setPhone(request.getPhone());
        college.setCollegeEmail(request.getCollegeEmail());
        college.setAddress(request.getAddress());

        collegeRepository.save(college);
        return mapToResponse(college);
    }

    public CollegeResponse getMyCollege(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found"));

        return mapToResponse(college);
    }

    public List<CollegeResponse> getAllApprovedColleges() {
        return collegeRepository.findByStatus(CollegeStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void checkCollegeApproved(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found"));

        if (college.getStatus() != CollegeStatus.APPROVED) {
            throw new UnauthorizedException("College not approved yet");
        }
    }

    // NEW — College dashboard stats
    public CollegeDashboardStats getDashboardStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found"));

        List<Fest> fests = festRepository.findByCollege(college);
        int totalFests = fests.size();

        List<Event> allEvents = fests.stream()
                .flatMap(fest -> eventRepository.findByFest(fest).stream())
                .collect(Collectors.toList());
        int totalEvents = allEvents.size();

        List<Registration> allRegistrations = allEvents.stream()
                .flatMap(event -> registrationRepository.findByEvent(event).stream())
                .collect(Collectors.toList());
        int totalRegistrations = allRegistrations.size();

        int totalConfirmed = (int) allRegistrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .count();

        int pendingPayments = (int) allRegistrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.PENDING)
                .count();

        double totalRevenue = allRegistrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .mapToDouble(Registration::getAmountPaid)
                .sum();

        return new CollegeDashboardStats(
                totalFests, totalEvents, totalRegistrations,
                totalConfirmed, totalRevenue, pendingPayments);
    }

    private CollegeResponse mapToResponse(College college) {
        return new CollegeResponse(
                college.getId(),
                college.getCollegeName(),
                college.getCity(),
                college.getState(),
                college.getPhone(),
                college.getCollegeEmail(),
                college.getAddress(),
                college.getStatus()
        );
    }
}