package com.campusapp.student;

import com.campusapp.auth.User;
import com.campusapp.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String fullName;

    @Column
    private String collegeName;

    @Column
    private String branch;

    @Column
    private String rollNumber;

    @Column
    private String year;

    @Column
    private String phone;

    @Column
    private String city;

    @Column
    private String state;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}