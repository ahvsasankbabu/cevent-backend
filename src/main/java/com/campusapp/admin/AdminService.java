package com.campusapp.admin;

import com.campusapp.auth.Role;
import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.college.College;
import com.campusapp.college.CollegeRepository;
import com.campusapp.college.CollegeStatus;
import com.campusapp.dto.AdminDashboardStats;
import com.campusapp.dto.CollegeResponse;
import com.campusapp.event.EventRepository;
import com.campusapp.event.FestRepository;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final FestRepository festRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public AdminService(CollegeRepository collegeRepository,
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

    public List<CollegeResponse> getAllColleges() {
        return collegeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CollegeResponse> getPendingColleges() {
        return collegeRepository.findByStatus(CollegeStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CollegeResponse approveCollege(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        college.setStatus(CollegeStatus.APPROVED);
        collegeRepository.save(college);
        return mapToResponse(college);
    }

    public CollegeResponse rejectCollege(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        college.setStatus(CollegeStatus.REJECTED);
        collegeRepository.save(college);
        return mapToResponse(college);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // NEW — Admin dashboard stats
    public AdminDashboardStats getDashboardStats() {
        int totalColleges = (int) collegeRepository.count();
        int pendingColleges = collegeRepository
                .findByStatus(CollegeStatus.PENDING).size();
        int approvedColleges = collegeRepository
                .findByStatus(CollegeStatus.APPROVED).size();
        int totalStudents = (int) userRepository.countByRole(Role.STUDENT);
        int totalFests = (int) festRepository.count();
        int totalEvents = (int) eventRepository.count();
        int totalRegistrations = (int) registrationRepository.count();

        double totalRevenue = registrationRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .mapToDouble(r -> r.getAmountPaid())
                .sum();

        return new AdminDashboardStats(
                totalColleges, pendingColleges, approvedColleges,
                totalStudents, totalFests, totalEvents,
                totalRegistrations, totalRevenue);
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