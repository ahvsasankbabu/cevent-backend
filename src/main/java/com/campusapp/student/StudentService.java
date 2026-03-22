package com.campusapp.student;

import com.campusapp.auth.User;
import com.campusapp.auth.UserRepository;
import com.campusapp.dto.StudentProfileRequest;
import com.campusapp.dto.StudentProfileResponse;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          UserRepository userRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    public StudentProfileResponse createProfile(String email,
                                                StudentProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (studentProfileRepository.existsByUser(user)) {
            throw new BadRequestException("Profile already exists. Use update instead.");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setCollegeName(request.getCollegeName());
        profile.setBranch(request.getBranch());
        profile.setRollNumber(request.getRollNumber());
        profile.setYear(request.getYear());
        profile.setPhone(request.getPhone());
        profile.setCity(request.getCity());
        profile.setState(request.getState());

        studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    public StudentProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapToResponse(profile);
    }

    public StudentProfileResponse updateProfile(String email,
                                                StudentProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getCollegeName() != null) profile.setCollegeName(request.getCollegeName());
        if (request.getBranch() != null) profile.setBranch(request.getBranch());
        if (request.getRollNumber() != null) profile.setRollNumber(request.getRollNumber());
        if (request.getYear() != null) profile.setYear(request.getYear());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());

        studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getCollegeName(),
                profile.getBranch(),
                profile.getRollNumber(),
                profile.getYear(),
                profile.getPhone(),
                profile.getCity(),
                profile.getState()
        );
    }
}