package com.campusapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusEventRegistrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusEventRegistrationApplication.class, args);
    }
}