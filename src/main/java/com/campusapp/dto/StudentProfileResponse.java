package com.campusapp.dto;

public class StudentProfileResponse {

    private Long id;
    private String fullName;
    private String collegeName;
    private String branch;
    private String rollNumber;
    private String year;
    private String phone;
    private String city;
    private String state;

    public StudentProfileResponse(Long id, String fullName, String collegeName,
                                   String branch, String rollNumber, String year,
                                   String phone, String city, String state) {
        this.id = id;
        this.fullName = fullName;
        this.collegeName = collegeName;
        this.branch = branch;
        this.rollNumber = rollNumber;
        this.year = year;
        this.phone = phone;
        this.city = city;
        this.state = state;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getCollegeName() { return collegeName; }
    public String getBranch() { return branch; }
    public String getRollNumber() { return rollNumber; }
    public String getYear() { return year; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getState() { return state; }
}