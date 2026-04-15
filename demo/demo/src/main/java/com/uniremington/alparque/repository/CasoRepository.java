package com.uniremington.alparque.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.Caso;

public interface CasoRepository extends JpaRepository<Caso, UUID> {
}