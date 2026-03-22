package com.campusapp.event;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // KEEP ALL ORIGINAL METHODS (for RegistrationService, SearchService)
    List<Event> findByFest(Fest fest);
    List<Event> findByFestAndActiveTrue(Fest fest);  // ← KEEP THIS!
    List<Event> findByFestAndCategory(Fest fest, EventCategory category);
    List<Event> findByActiveTrue();

    // NEW METHODS (for College dashboard - ALL events)
    @Query("SELECT e FROM Event e WHERE e.fest.id = :festId ORDER BY e.id DESC")
    List<Event> findByFestId(@Param("festId") Long festId);

    @Query("SELECT e FROM Event e WHERE e.fest.id = :festId AND e.active = true ORDER BY e.id DESC")
    List<Event> findByFestAndActiveTrue(@Param("festId") Long festId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") Long id);
}
