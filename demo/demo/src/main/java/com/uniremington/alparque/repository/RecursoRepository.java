package com.uniremington.alparque.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.Recurso;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
}
