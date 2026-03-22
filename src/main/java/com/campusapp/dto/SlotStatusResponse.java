package com.campusapp.dto;

public class SlotStatusResponse {

    private int availableSlots;
    private int totalSlots;
    private int confirmedCount;
    private int pendingCount;
    private String slotStatus;

    public SlotStatusResponse(int availableSlots, int totalSlots,
                               int confirmedCount, int pendingCount,
                               String slotStatus) {
        this.availableSlots = availableSlots;
        this.totalSlots = totalSlots;
        this.confirmedCount = confirmedCount;
        this.pendingCount = pendingCount;
        this.slotStatus = slotStatus;
    }

    public int getAvailableSlots() { return availableSlots; }
    public int getTotalSlots() { return totalSlots; }
    public int getConfirmedCount() { return confirmedCount; }
    public int getPendingCount() { return pendingCount; }
    public String getSlotStatus() { return slotStatus; }
}