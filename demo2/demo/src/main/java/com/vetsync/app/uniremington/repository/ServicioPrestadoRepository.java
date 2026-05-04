package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.ServicioPrestado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioPrestadoRepository extends JpaRepository<ServicioPrestado, String> {

    List<ServicioPrestado> findByBeneficiarioId(String beneficiarioId);
}
