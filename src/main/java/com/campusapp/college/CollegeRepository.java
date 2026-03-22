package com.campusapp.college;

import com.campusapp.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByUser(User user);

    Optional<College> findByCollegeEmail(String collegeEmail);

    List<College> findByStatus(CollegeStatus status);

    boolean existsByCollegeEmail(String collegeEmail);

    boolean existsByUser(User user);
}