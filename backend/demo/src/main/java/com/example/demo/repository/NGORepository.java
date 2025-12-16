package com.example.demo.repository;

import com.example.demo.entity.NGO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NGORepository extends JpaRepository<NGO, Integer> {
    boolean existsByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);
    NGO findByEmail(String email);
}

