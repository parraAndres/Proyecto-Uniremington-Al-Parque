package com.uniremington.alparque.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.Diagnostico;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, UUID> {
}
