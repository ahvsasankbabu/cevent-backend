package com.campusapp.dto;

import com.campusapp.certificate.CertificateStatus;
import java.time.LocalDateTime;

public class CertificateResponse {

    private Long id;
    private String certificateId;
    private String studentName;
    private String eventName;
    private String festName;
    private String certificatePath;
    private String qrCodePath;
    private CertificateStatus status;
    private LocalDateTime issuedAt;

    public CertificateResponse(Long id, String certificateId,
                                String studentName, String eventName,
                                String festName, String certificatePath,
                                String qrCodePath, CertificateStatus status,
                                LocalDateTime issuedAt) {
        this.id = id;
        this.certificateId = certificateId;
        this.studentName = studentName;
        this.eventName = eventName;
        this.festName = festName;
        this.certificatePath = certificatePath;
        this.qrCodePath = qrCodePath;
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public Long getId() { return id; }
    public String getCertificateId() { return certificateId; }
    public String getStudentName() { return studentName; }
    public String getEventName() { return eventName; }
    public String getFestName() { return festName; }
    public String getCertificatePath() { return certificatePath; }
    public String getQrCodePath() { return qrCodePath; }
    public CertificateStatus getStatus() { return status; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
}