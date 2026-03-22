package com.campusapp.certificate;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.CertificateResponse;
import com.campusapp.dto.CertificateStatusResponse;
import com.campusapp.dto.FieldMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/template/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("festId") Long festId,
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam("scope") String scope,
            Principal principal) {
        try {
            Map<String, Object> result = certificateService.uploadTemplate(
                    principal.getName(), file, festId, eventId, scope);
            if ((boolean) result.get("valid")) {
                return ResponseEntity.ok(
                        ApiResponse.success("Template uploaded", result));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failure(
                                (String) result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @PostMapping("/template/{templateId}/mappings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveFieldMappings(
            @PathVariable Long templateId,
            @RequestBody List<FieldMapping> mappings) {
        try {
            Map<String, Object> result = certificateService
                    .saveFieldMappings(templateId, mappings);
            if ((boolean) result.get("valid")) {
                return ResponseEntity.ok(
                        ApiResponse.success("Mappings saved", result));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failure(
                                (String) result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @PostMapping("/generate/event/{eventId}")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> generateCertificates(
            @PathVariable Long eventId,
            Principal principal) {
        try {
            List<CertificateResponse> responses = certificateService
                    .generateCertificates(principal.getName(), eventId);
            return ResponseEntity.ok(
                    ApiResponse.success("Certificates generated", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping("/my/{registrationId}")
    public ResponseEntity<ApiResponse<CertificateResponse>> getMyCertificate(
            @PathVariable Long registrationId,
            Principal principal) {
        CertificateResponse response = certificateService
                .getMyCertificate(principal.getName(), registrationId);
        return ResponseEntity.ok(
                ApiResponse.success("Certificate fetched", response));
    }

    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<ApiResponse<CertificateResponse>> verifyCertificate(
            @PathVariable String certificateId) {
        CertificateResponse response = certificateService
                .verifyCertificate(certificateId);
        return ResponseEntity.ok(
                ApiResponse.success("Certificate verified", response));
    }

    // NEW — Get certificate status for an event
    @GetMapping("/status/event/{eventId}")
    public ResponseEntity<ApiResponse<CertificateStatusResponse>> getCertificateStatus(
            @PathVariable Long eventId) {
        CertificateStatusResponse response = certificateService
                .getCertificateStatus(eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Certificate status fetched", response));
    }

    // NEW — Get all certificates for logged in student
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getMyCertificates(
            Principal principal) {
        List<CertificateResponse> responses = certificateService
                .getMyCertificates(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Certificates fetched", responses));
    }
    
    @GetMapping("/download/{certificateId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadCertificate(
            @PathVariable String certificateId) throws java.io.IOException {
        com.campusapp.certificate.Certificate cert = certificateService
                .getCertificateByIdRaw(certificateId);
        
        java.nio.file.Path path = java.nio.file.Paths.get(cert.getCertificatePath());
        org.springframework.core.io.Resource resource = 
                new org.springframework.core.io.FileSystemResource(path);
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + certificateId + ".pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(resource);
    }
}