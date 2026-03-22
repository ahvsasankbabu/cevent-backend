package com.campusapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CollegeRegisterRequest {

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "College email is required")
    @Email(message = "Invalid email format")
    private String collegeEmail;

    @NotBlank(message = "Address is required")
    private String address;

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
}