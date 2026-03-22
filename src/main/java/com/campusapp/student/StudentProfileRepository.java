package com.campusapp.student;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusapp.auth.User;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUser(User user);

    boolean existsByUser(User user);
}