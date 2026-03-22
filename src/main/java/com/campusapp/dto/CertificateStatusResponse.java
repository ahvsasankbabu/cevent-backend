package com.campusapp.dto;

public class CertificateStatusResponse {

    private boolean generated;
    private int count;
    private int totalConfirmed;

    public CertificateStatusResponse(boolean generated, int count,
                                      int totalConfirmed) {
        this.generated = generated;
        this.count = count;
        this.totalConfirmed = totalConfirmed;
    }

    public boolean isGenerated() { return generated; }
    public int getCount() { return count; }
    public int getTotalConfirmed() { return totalConfirmed; }
}