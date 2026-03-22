package com.campusapp.certificate;

import com.campusapp.event.Event;
import com.campusapp.event.Fest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertificateTemplateRepository 
        extends JpaRepository<CertificateTemplate, Long> {

    Optional<CertificateTemplate> findByEventAndActiveTrue(Event event);

    Optional<CertificateTemplate> findByFestAndScopeAndActiveTrue(
            Fest fest, TemplateScope scope);
}