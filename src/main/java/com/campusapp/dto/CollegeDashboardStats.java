package com.campusapp.dto;

public class CollegeDashboardStats {

    private int totalFests;
    private int totalEvents;
    private int totalRegistrations;
    private int totalConfirmed;
    private double totalRevenue;
    private int pendingPayments;

    public CollegeDashboardStats(int totalFests, int totalEvents,
                                  int totalRegistrations, int totalConfirmed,
                                  double totalRevenue, int pendingPayments) {
        this.totalFests = totalFests;
        this.totalEvents = totalEvents;
        this.totalRegistrations = totalRegistrations;
        this.totalConfirmed = totalConfirmed;
        this.totalRevenue = totalRevenue;
        this.pendingPayments = pendingPayments;
    }

    public int getTotalFests() { return totalFests; }
    public int getTotalEvents() { return totalEvents; }
    public int getTotalRegistrations() { return totalRegistrations; }
    public int getTotalConfirmed() { return totalConfirmed; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getPendingPayments() { return pendingPayments; }
}