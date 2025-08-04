package com.reports.utility;

import com.reports.entity.Doctor;
import com.reports.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DoctorDetailsService implements UserDetailsService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Doctor doctor = doctorRepo.findByEmail(email);
        if (doctor == null) {
            throw new UsernameNotFoundException("Doctor not found with email: " + email);
        }

        return new User(
                doctor.getEmail(),
                doctor.getPassword(),
                Collections.emptyList()
        );
    }
}
