package com.campusapp.college;

import com.campusapp.auth.User;
import com.campusapp.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "colleges")
public class College extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String collegeName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String collegeEmail;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollegeStatus status = CollegeStatus.PENDING;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCollegeEmail() { return collegeEmail; }
    public void setCollegeEmail(String collegeEmail) { this.collegeEmail = collegeEmail; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public CollegeStatus getStatus() { return status; }
    public void setStatus(CollegeStatus status) { this.status = status; }
}