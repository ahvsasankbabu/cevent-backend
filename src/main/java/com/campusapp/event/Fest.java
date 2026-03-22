package com.campusapp.event;

import com.campusapp.college.College;
import com.campusapp.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "fests")
public class Fest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double startingFee;

    @Column(nullable = false)
    private boolean active = true;

    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getStartingFee() { return startingFee; }
    public void setStartingFee(Double startingFee) { this.startingFee = startingFee; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}