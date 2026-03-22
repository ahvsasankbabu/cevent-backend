package com.campusapp.certificate;

import com.campusapp.common.BaseEntity;
import com.campusapp.event.Event;
import com.campusapp.event.Fest;
import jakarta.persistence.*;

@Entity
@Table(name = "certificate_templates")
public class CertificateTemplate extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "fest_id", nullable = false)
    private Fest fest;
    
    @Column(columnDefinition = "TEXT")
    private String fieldMappings;
    
    public String getFieldMappings() {
		return fieldMappings;
	}
	public void setFieldMappings(String fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
	@ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private String templatePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateScope scope;

    @Column(nullable = false)
    private boolean active = true;

    public Fest getFest() { return fest; }
    public void setFest(Fest fest) { this.fest = fest; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getTemplatePath() { return templatePath; }
    public void setTemplatePath(String templatePath) { this.templatePath = templatePath; }

    public TemplateScope getScope() { return scope; }
    public void setScope(TemplateScope scope) { this.scope = scope; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}