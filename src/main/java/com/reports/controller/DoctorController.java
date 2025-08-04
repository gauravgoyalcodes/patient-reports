package com.reports.controller;

import com.reports.entity.Doctor;
import com.reports.service.DoctorService;

import com.reports.utility.AuthRequest;
import com.reports.utility.AuthResponse;
import com.reports.utility.DoctorDetailsService;
import com.reports.utility.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DoctorDetailsService doctorDetailsService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        final UserDetails userDetails = doctorDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

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
