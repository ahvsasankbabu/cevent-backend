package com.campusapp.dto;

public class AdminDashboardStats {

    private int totalColleges;
    private int pendingColleges;
    private int approvedColleges;
    private int totalStudents;
    private int totalFests;
    private int totalEvents;
    private int totalRegistrations;
    private double totalRevenue;

    public AdminDashboardStats(int totalColleges, int pendingColleges,
                                int approvedColleges, int totalStudents,
                                int totalFests, int totalEvents,
                                int totalRegistrations, double totalRevenue) {
        this.totalColleges = totalColleges;
        this.pendingColleges = pendingColleges;
        this.approvedColleges = approvedColleges;
        this.totalStudents = totalStudents;
        this.totalFests = totalFests;
        this.totalEvents = totalEvents;
        this.totalRegistrations = totalRegistrations;
        this.totalRevenue = totalRevenue;
    }

    public int getTotalColleges() { return totalColleges; }
    public int getPendingColleges() { return pendingColleges; }
    public int getApprovedColleges() { return approvedColleges; }
    public int getTotalStudents() { return totalStudents; }
    public int getTotalFests() { return totalFests; }
    public int getTotalEvents() { return totalEvents; }
    public int getTotalRegistrations() { return totalRegistrations; }
    public double getTotalRevenue() { return totalRevenue; }
}