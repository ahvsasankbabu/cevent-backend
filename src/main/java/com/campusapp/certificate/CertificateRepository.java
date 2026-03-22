package com.campusapp.certificate;

import com.campusapp.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository 
        extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByRegistration(Registration registration);

    Optional<Certificate> findByCertificateId(String certificateId);

    List<Certificate> findByRegistration_Event_Fest_Id(Long festId);

    List<Certificate> findByRegistration_Event_Id(Long eventId);
}