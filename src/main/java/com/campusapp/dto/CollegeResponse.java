package com.campusapp.dto;

import com.campusapp.college.CollegeStatus;

public class CollegeResponse {

    private Long id;
    private String collegeName;
    private String city;
    private String state;
    private String phone;
    private String collegeEmail;
    private String address;
    private CollegeStatus status;

    public CollegeResponse(Long id, String collegeName, String city,
                           String state, String phone, String collegeEmail,
                           String address, CollegeStatus status) {
        this.id = id;
        this.collegeName = collegeName;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.collegeEmail = collegeEmail;
        this.address = address;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getCollegeName() { return collegeName; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPhone() { return phone; }
    public String getCollegeEmail() { return collegeEmail; }
    public String getAddress() { return address; }
    public CollegeStatus getStatus() { return status; }
}