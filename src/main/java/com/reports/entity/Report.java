package com.reports.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private LocalDate uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor assignedDoctor;

    // Constructors
    public Report() {}

    public Report(String fileName, String filePath, LocalDate uploadDate, Doctor assignedDoctor) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.assignedDoctor = assignedDoctor;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public Doctor getAssignedDoctor() { return assignedDoctor; }
    public void setAssignedDoctor(Doctor assignedDoctor) { this.assignedDoctor = assignedDoctor; }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uploadDate=" + uploadDate +
                ", assignedDoctor=" + assignedDoctor +
                '}';
    }
}

