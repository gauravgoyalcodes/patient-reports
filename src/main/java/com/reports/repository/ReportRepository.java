package com.reports.repository;

import com.reports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByAssignedDoctorDoctorId(String doctorId);
}


