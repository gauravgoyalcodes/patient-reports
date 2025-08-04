package com.reports.service;

import com.reports.entity.Doctor;
import com.reports.entity.Report;
import com.reports.repository.DoctorRepository;
import com.reports.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.aspectj.weaver.NameMangler.PREFIX;


@Service
public class DoctorService {
    Logger log = LoggerFactory.getLogger(DoctorService.class);

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PREFIX = "DR";

    //registering a doctor
    public void registerDoctor(Doctor doctor) {
        log.info("checking if the doctor is present in the db !! " + doctorRepository.findByEmail(doctor.getEmail()));
        try {
            String doctorId = generateDoctorId();
            log.info("Generated Doctor ID: " + doctorId);
            doctor.setDoctorId(doctorId);
            doctor.setCreatedAt(LocalDateTime.now());
            doctor.setUpdatedAt(LocalDateTime.now());
            doctor.setActive(true);
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            log.info("Final doctor object before saving: {}", doctor);
            doctorRepository.save(doctor);
            log.info("Doctor saved successfully: {}", doctor);
        } catch (DataIntegrityViolationException e) {
            log.error("⚠️ Data integrity violation: {}", e.getMessage(), e);
            throw new RuntimeException("Database constraint violation. See logs.");
        }
    }

    //fetching list of all doctors
    public List<Doctor> getListOfAllDoctors() {
        log.info("fetching list of all doctors");
        return doctorRepository.findAll();
    }

    //get logged in doctor details
    public Doctor getDoctorDetails(String email) {
        return doctorRepository.findByEmail(email);
    }


    //get reports associated to doctor
    public List<Report> getReportAssociatedtoDoctor(String email){
        String doctorId = doctorRepository.findByEmail(email).getDoctorId();
        List<Report> reports = reportRepository.findByAssignedDoctorDoctorId(doctorId);
        return reports;
    }

    public String generateDoctorId() {

        String lastId = doctorRepository.findLastDoctorId(); // e.g., "DR0001"
        int nextNumber = 1;

        if (lastId != null && lastId.startsWith(PREFIX)) {
            String numberPart = lastId.substring(PREFIX.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        return PREFIX + String.format("%04d", nextNumber);  // e.g., "DR0002"
    }

}
