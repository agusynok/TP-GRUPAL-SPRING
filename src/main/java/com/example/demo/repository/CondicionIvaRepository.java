package com.example.demo.repository;

import com.example.demo.model.CondicionIva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondicionIvaRepository extends JpaRepository<CondicionIva, Long> {
}
