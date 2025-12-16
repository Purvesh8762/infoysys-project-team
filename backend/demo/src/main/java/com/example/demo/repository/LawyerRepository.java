package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Lawyer;

public interface LawyerRepository extends JpaRepository<Lawyer, Integer> {
    boolean existsByEmail(String email);
    boolean existsByAadharNum(String aadharNum);
    boolean existsByBarCouncilId(String barCouncilId);
    Lawyer findByEmail(String email);
}
