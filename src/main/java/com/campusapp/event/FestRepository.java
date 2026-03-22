package com.campusapp.event;

import com.campusapp.college.College;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FestRepository extends JpaRepository<Fest, Long> {

    List<Fest> findByCollege(College college);

    List<Fest> findByActiveTrue();

    List<Fest> findByCollegeAndActiveTrue(College college);
}