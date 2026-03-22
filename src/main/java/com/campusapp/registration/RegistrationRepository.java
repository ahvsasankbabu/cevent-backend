package com.campusapp.registration;

import com.campusapp.auth.User;
import com.campusapp.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.student = :student " +
           "AND r.event = :event AND r.status IN ('PENDING', 'CONFIRMED')")
    boolean existsActiveRegistration(@Param("student") User student,
                                     @Param("event") Event event);

    List<Registration> findByStudent(User student);

    List<Registration> findByEvent(Event event);

    List<Registration> findByStudentAndStatus(User student,
                                               RegistrationStatus status);

    Optional<Registration> findByStudentAndEvent(User student, Event event);

    int countByEventAndStatus(Event event, RegistrationStatus status);

    @Query("SELECT r FROM Registration r WHERE r.status = 'PENDING' " +
           "AND r.expiresAt IS NOT NULL AND r.expiresAt < :now")
    List<Registration> findExpiredPendingRegistrations(
            @Param("now") LocalDateTime now);

    int countByEventAndStatusIn(Event event, List<RegistrationStatus> statuses);

    // NEW — for participants list
    List<Registration> findByEventAndStatus(Event event,
                                             RegistrationStatus status);
}