package com.campusapp.certificate;

import com.campusapp.event.Event;
import com.campusapp.event.Fest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateTemplateRepository
        extends JpaRepository<CertificateTemplate, Long> {

    List<CertificateTemplate> findByEventAndActiveTrueOrderByIdDesc(Event event);

    List<CertificateTemplate> findByFestAndScopeAndActiveTrueOrderByIdDesc(
            Fest fest, TemplateScope scope);
}