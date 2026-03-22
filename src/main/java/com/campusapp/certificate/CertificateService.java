package com.campusapp.certificate;

import com.campusapp.auth.User;
import com.campusapp.dto.CertificateStatusResponse;
import java.util.stream.Collectors;
import com.campusapp.auth.UserRepository;
import com.campusapp.dto.CertificateResponse;
import com.campusapp.dto.FieldMapping;
import com.campusapp.event.Event;
import com.campusapp.event.EventRepository;
import com.campusapp.event.Fest;
import com.campusapp.event.FestRepository;
import com.campusapp.exception.BadRequestException;
import com.campusapp.exception.ResourceNotFoundException;
import com.campusapp.registration.Registration;
import com.campusapp.registration.RegistrationRepository;
import com.campusapp.registration.RegistrationStatus;
import com.campusapp.student.StudentProfile;
import com.campusapp.student.StudentProfileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CertificateService {

    private static final String UPLOAD_DIR = "uploads/templates/";
    private static final String CERT_DIR = "uploads/certificates/";
    private static final String QR_DIR = "uploads/qrcodes/";

    private static final Set<String> VALID_PLACEHOLDERS = new HashSet<>(Arrays.asList(
            "{{student_name}}", "{{roll_number}}", "{{branch}}",
            "{{college_name}}", "{{event_name}}", "{{fest_name}}",
            "{{organizer_name}}", "{{date}}", "{{year}}",
            "{{certificate_id}}"
    ));

    private final CertificateTemplateRepository templateRepository;
    private final CertificateRepository certificateRepository;
    private final RegistrationRepository registrationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EventRepository eventRepository;
    private final FestRepository festRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public CertificateService(CertificateTemplateRepository templateRepository,
                               CertificateRepository certificateRepository,
                               RegistrationRepository registrationRepository,
                               StudentProfileRepository studentProfileRepository,
                               EventRepository eventRepository,
                               FestRepository festRepository,
                               UserRepository userRepository,
                               ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.certificateRepository = certificateRepository;
        this.registrationRepository = registrationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.eventRepository = eventRepository;
        this.festRepository = festRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> uploadTemplate(String email, MultipartFile file,
                                               Long festId, Long eventId,
                                               String scope) throws IOException {
        validateTemplate(file);

        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        Fest fest = festRepository.findById(festId)
                .orElseThrow(() -> new ResourceNotFoundException("Fest not found"));

        CertificateTemplate template = new CertificateTemplate();
        template.setFest(fest);
        template.setTemplatePath(filePath.toString());
        template.setScope(TemplateScope.valueOf(scope.toUpperCase()));

        if (eventId != null && scope.equalsIgnoreCase("EVENT")) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            template.setEvent(event);
        }

        templateRepository.save(template);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Template uploaded successfully. Now save field mappings.");
        response.put("templateId", template.getId());
        return response;
    }

    public Map<String, Object> saveFieldMappings(Long templateId,
                                                   List<FieldMapping> mappings)
            throws IOException {
        CertificateTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        for (FieldMapping mapping : mappings) {
            if (!VALID_PLACEHOLDERS.contains(mapping.getPlaceholder())) {
                Map<String, Object> error = new HashMap<>();
                error.put("valid", false);
                error.put("message", "Invalid placeholder: " + mapping.getPlaceholder());
                error.put("validPlaceholders", new ArrayList<>(VALID_PLACEHOLDERS));
                return error;
            }
        }

        String mappingsJson = objectMapper.writeValueAsString(mappings);
        template.setFieldMappings(mappingsJson);
        templateRepository.save(template);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Field mappings saved successfully");
        return response;
    }

    public List<CertificateResponse> generateCertificates(String email,
                                                           Long eventId)
            throws IOException, WriterException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        CertificateTemplate template = getTemplate(event);

        if (template.getFieldMappings() == null) {
            throw new BadRequestException(
                    "Field mappings not set. Please map fields before generating.");
        }

        List<FieldMapping> mappings = objectMapper.readValue(
                template.getFieldMappings(),
                new TypeReference<List<FieldMapping>>() {});

        List<Registration> registrations = registrationRepository
                .findByEvent(event)
                .stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .toList();

        if (registrations.isEmpty()) {
            throw new BadRequestException("No confirmed registrations found");
        }

        List<CertificateResponse> responses = new ArrayList<>();

        for (Registration registration : registrations) {
            if (certificateRepository.findByRegistration(registration).isPresent()) {
                continue;
            }

            User student = registration.getStudent();
            StudentProfile profile = studentProfileRepository
                    .findByUser(student)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student profile not found for " + student.getEmail()));

            String certId = "CERT-" + UUID.randomUUID().toString()
                    .substring(0, 8).toUpperCase();

            String qrPath = generateQRCode(certId);
            String certPath = generateCertificateFromPdf(
                    template, profile, event, certId, qrPath, mappings);

            Certificate certificate = new Certificate();
            certificate.setRegistration(registration);
            certificate.setCertificatePath(certPath);
            certificate.setQrCodePath(qrPath);
            certificate.setCertificateId(certId);
            certificate.setStatus(CertificateStatus.GENERATED);
            certificate.setIssuedAt(LocalDateTime.now());

            certificateRepository.save(certificate);
            responses.add(mapToResponse(certificate));
        }

        return responses;
    }

    public CertificateResponse getMyCertificate(String email, Long registrationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found"));

        Certificate certificate = certificateRepository
                .findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found"));

        return mapToResponse(certificate);
    }

    public CertificateResponse verifyCertificate(String certificateId) {
        Certificate certificate = certificateRepository
                .findByCertificateId(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found or invalid"));

        return mapToResponse(certificate);
    }
 // NEW — Get certificate status for an event
    public CertificateStatusResponse getCertificateStatus(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        int totalConfirmed = registrationRepository.countByEventAndStatus(
                event, RegistrationStatus.CONFIRMED);

        List<Certificate> certificates = certificateRepository
                .findByRegistration_Event_Id(eventId);

        int generatedCount = (int) certificates.stream()
                .filter(c -> c.getStatus() == CertificateStatus.GENERATED)
                .count();

        boolean generated = generatedCount > 0;

        return new CertificateStatusResponse(generated, generatedCount, totalConfirmed);
    }

    // NEW — Get all certificates for logged in student
    public List<CertificateResponse> getMyCertificates(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Registration> registrations = registrationRepository
                .findByStudent(user);

        return registrations.stream()
                .map(registration -> certificateRepository
                        .findByRegistration(registration)
                        .orElse(null))
                .filter(certificate -> certificate != null)
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private CertificateTemplate getTemplate(Event event) {
        List<CertificateTemplate> eventTemplates = templateRepository
                .findByEventAndActiveTrueOrderByIdDesc(event);
        if (!eventTemplates.isEmpty()) return eventTemplates.get(0);

        List<CertificateTemplate> festTemplates = templateRepository
                .findByFestAndScopeAndActiveTrueOrderByIdDesc(event.getFest(), TemplateScope.FEST);
        if (!festTemplates.isEmpty()) return festTemplates.get(0);

        throw new ResourceNotFoundException("No certificate template found for this event");
    }

    private String generateCertificateFromPdf(CertificateTemplate template,
                                               StudentProfile profile,
                                               Event event, String certId,
                                               String qrPath,
                                               List<FieldMapping> mappings)
            throws IOException {

        File templateFile = new File(template.getTemplatePath());

        try (PDDocument document = Loader.loadPDF(templateFile)) {
            PDPage page = document.getPage(0);

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page,
                    PDPageContentStream.AppendMode.APPEND, true, true)) {

                for (FieldMapping mapping : mappings) {
                    String value = resolvePlaceholder(mapping.getPlaceholder(),
                            profile, event, certId);

                    PDType1Font font = resolveFont(mapping.getFontName());
                    float fontSize = mapping.getFontSize() > 0 ?
                            mapping.getFontSize() : 12;

                    Color color = parseColor(mapping.getColor());

                    contentStream.beginText();
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(color);
                    contentStream.newLineAtOffset(mapping.getX(), mapping.getY());
                    contentStream.showText(value);
                    contentStream.endText();
                }

                PDImageXObject qrImage = PDImageXObject.createFromFile(
                        qrPath, document);
                contentStream.drawImage(qrImage, 30, 30, 60, 60);
            }

            Files.createDirectories(Paths.get(CERT_DIR));
            String certPath = CERT_DIR + certId + "_certificate.pdf";
            document.save(certPath);
            return certPath;
        }
    }

    private String resolvePlaceholder(String placeholder,
                                       StudentProfile profile,
                                       Event event, String certId) {
        switch (placeholder) {
            case "{{student_name}}":
                return profile.getFullName() != null ? profile.getFullName() : "";
            case "{{roll_number}}":
                return profile.getRollNumber() != null ? profile.getRollNumber() : "";
            case "{{branch}}":
                return profile.getBranch() != null ? profile.getBranch() : "";
            case "{{college_name}}":
                return profile.getCollegeName() != null ? profile.getCollegeName() : "";
            case "{{event_name}}":
                return event.getName();
            case "{{fest_name}}":
                return event.getFest().getName();
            case "{{organizer_name}}":
                return event.getFest().getCollege().getCollegeName();
            case "{{date}}":
                return event.getEventDate().toString();
            case "{{year}}":
                return profile.getYear() != null ? profile.getYear() : "";
            case "{{certificate_id}}":
                return certId;
            default:
                return "";
        }
    }

    private PDType1Font resolveFont(String fontName) {
        if (fontName == null) {
            return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        }
        switch (fontName.toUpperCase()) {
            case "HELVETICA_BOLD":
                return new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            case "TIMES_ROMAN":
                return new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
            case "TIMES_BOLD":
                return new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
            case "COURIER":
                return new PDType1Font(Standard14Fonts.FontName.COURIER);
            case "COURIER_BOLD":
                return new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            default:
                return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        }
    }

    private Color parseColor(String colorHex) {
        if (colorHex == null || colorHex.isEmpty()) {
            return Color.BLACK;
        }
        try {
            return Color.decode(colorHex);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    private void validateTemplate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Template file is empty");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".pdf")) {
            throw new BadRequestException("Only PDF template files are supported");
        }
    }

    private String generateQRCode(String certId)
            throws WriterException, IOException {
        String verificationUrl = "http://localhost:8080/api/v1/certificates/verify/"
                + certId;
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(verificationUrl,
                BarcodeFormat.QR_CODE, 200, 200);

        Files.createDirectories(Paths.get(QR_DIR));
        String qrPath = QR_DIR + certId + "_qr.png";
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(qrPath));
        return qrPath;
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        Registration registration = certificate.getRegistration();
        return new CertificateResponse(
                certificate.getId(),
                certificate.getCertificateId(),
                registration.getStudent().getName(),
                registration.getEvent().getName(),
                registration.getEvent().getFest().getName(),
                certificate.getCertificatePath(),
                certificate.getQrCodePath(),
                certificate.getStatus(),
                certificate.getIssuedAt()
        );
    }
    public Certificate getCertificateByIdRaw(String certificateId) {
        return certificateRepository.findByCertificateId(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
    }
}