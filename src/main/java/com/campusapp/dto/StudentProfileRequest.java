package com.campusapp.dto;

import jakarta.validation.constraints.NotBlank;

public class StudentProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String collegeName;
    private String branch;
    private String rollNumber;
    private String year;
    private String phone;
    private String city;
    private String state;

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