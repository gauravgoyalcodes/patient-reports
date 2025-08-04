package com.reports.controller;

import com.reports.service.ReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/reports")
public class ReportController {
    Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadReport(@RequestPart("file") MultipartFile file) {
        log.info("File recieved on the server :" + file);
        try {
            String response = reportService.processAndSaveReport(file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading PDF file.");
        }
    }
}
