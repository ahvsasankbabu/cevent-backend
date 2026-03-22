package com.campusapp.certificate;

import com.campusapp.common.BaseEntity;
import com.campusapp.registration.Registration;
import jakarta.persistence.*;

@Entity
@Table(name = "certificates")
public class Certificate extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @Column(nullable = false)
    private String certificatePath;

    @Column(nullable = false)
    private String qrCodePath;

    @Column(nullable = false, unique = true)
    private String certificateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status = CertificateStatus.PENDING;

    @Column
    private java.time.LocalDateTime issuedAt;

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) { this.registration = registration; }

    public String getCertificatePath() { return certificatePath; }
    public void setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; }

    public String getQrCodePath() { return qrCodePath; }
    public void setQrCodePath(String qrCodePath) { this.qrCodePath = qrCodePath; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public java.time.LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(java.time.LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}