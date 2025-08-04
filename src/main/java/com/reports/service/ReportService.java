package com.reports.service;

import com.reports.entity.Doctor;
import com.reports.entity.Report;
import com.reports.repository.DoctorRepository;
import com.reports.repository.ReportRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReportService {
    Logger log = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private ReportRepository reportRepo;

    public String processAndSaveReport(MultipartFile file) throws IOException {
        // Step 1: Extract text from PDF
        String text = extractTextFromPdf(file);
        log.info("Extracted PDF Text:\n" + text);

        // Step 2: Parse doctor name
        String extractedDoctorName = extractDoctorName(text);
        log.info("Doctor name extracted: " + extractedDoctorName);

        if (extractedDoctorName == null) {
            throw new IllegalArgumentException("Doctor name not found in PDF.");
        }

        //parse patient name
        String extractedPatientName = extractPatientName(text);
        log.info("Patient name extracted: " + extractedPatientName);

//        if (extractedPatientName == null) {
//            throw new IllegalArgumentException("Patient name not found in PDF.");
//        }

        //parse patient age
        String extractedPatientAge = extractPatientAge(text);
        log.info("Patient age extracted: " + extractedPatientAge);

//        if (extractedPatientAge == null) {
//            throw new IllegalArgumentException("Patient Age not found in PDF.");
//        }

        // Step 3: Normalize doctor name (remove prefix)
        String normalizedDoctorName = normalizeDoctorName(extractedDoctorName);
        log.info("Normalized doctor name: " + normalizedDoctorName);

        // Step 4: Lookup doctor in DB (names stored without prefix)
        Doctor doctor = doctorRepo.findByDoctorName(normalizedDoctorName);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor '" + normalizedDoctorName + "' not found in system.");
        }

        // Step 5: Save file locally
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Step 6: Save report metadata
        Report report = new Report();
        if (extractedPatientName != null){
            report.setPatientName(extractedPatientName);
        }
        if (extractedPatientAge != null){
            report.setPatientAge(extractedPatientAge);
        }
        report.setFileName(file.getOriginalFilename());
        report.setFilePath(filePath.toString());
        report.setUploadDate(LocalDate.now());
        report.setAssignedDoctor(doctor);
        reportRepo.save(report);

        return "Report uploaded and assigned to Dr. " + normalizedDoctorName;
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractDoctorName(String text) {
        String normalizedText = text.replaceAll("[\\r\\n]+", "\n").replaceAll("\\s+", " ");

        Pattern pattern = Pattern.compile(
                "(?i)Refd\\. By\\s+(Dr\\.\\s+[A-Z][a-zA-Z\\s\\.]+?)(?=\\s+(Printed On|Client|Page|\\d|$))"
        );

        Matcher matcher = pattern.matcher(normalizedText);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        for (String line : text.split("\n")) {
            if (line.toLowerCase().contains("refd. by")) {
                String[] parts = line.split("refd. by", 2);
                if (parts.length > 1) {
                    String candidate = parts[1].trim();
                    candidate = candidate.split("Printed On|Client|Page")[0].trim();
                    return candidate;
                }
            }
        }

        return null;
    }

    private String extractPatientName(String text) {
        Pattern pattern = Pattern.compile("(?i)Patient Name\\s+(Mr\\.?|Mrs\\.?|Ms\\.?|Miss)?\\s*([A-Z\\s]+?)(?=\\s+Gender|\\r|\\n)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

    private String extractPatientAge(String text) {
        Pattern pattern = Pattern.compile("(?i)Gender\\s*/\\s*Age\\s+[A-Za-z]+\\s*/\\s*(\\d{1,3})\\s*Yrs");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String normalizeDoctorName(String name) {
        if (name == null) return null;
        return name.replaceAll("(?i)^(dr\\.|mr\\.|ms\\.|mrs\\.|prof\\.)\\s*", "").trim();
    }
}