package com.reports.controller;

import com.reports.entity.Doctor;
import com.reports.service.DoctorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@Controller
@RequestMapping("/doctor")
public class DoctorController {
    Logger log = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<String> registerNewDoctor(@RequestBody Doctor doctor) {
        try {
            doctorService.registerDoctor(doctor);
            return ResponseEntity.ok("User is successfully registered");
        } catch (RuntimeException ex) {
            log.error("Doctor registration failed: {}", ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + ex.getMessage());
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {

        List<Doctor> doctors = doctorService.getListOfAllDoctors();
        if (doctors == null || doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK); // 200
    }



}
