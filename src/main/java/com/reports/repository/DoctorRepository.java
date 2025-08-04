package com.reports.repository;

import com.reports.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.doctorName) = LOWER(:doctorName)")
    Doctor findByDoctorName(@Param("doctorName") String doctorName);

    Doctor findByEmail(String email);

    @Query(value = "SELECT DOCTOR_ID FROM DOCTORS ORDER BY DOCTOR_ID DESC LIMIT 1", nativeQuery = true)
    String findLastDoctorId();

}

