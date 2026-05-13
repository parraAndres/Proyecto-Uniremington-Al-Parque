package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.ConfiguracionParametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfiguracionParametroRepository extends JpaRepository<ConfiguracionParametro, Long> {
    List<ConfiguracionParametro> findByTipoAndActivoTrue(String tipo);
    List<ConfiguracionParametro> findByTipo(String tipo);
}
