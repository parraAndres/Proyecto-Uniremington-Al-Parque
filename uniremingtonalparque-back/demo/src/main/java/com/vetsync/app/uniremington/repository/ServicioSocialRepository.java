package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.ServicioSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ServicioSocialRepository extends JpaRepository<ServicioSocial, Long> {

    @Query("""
        select s.facultad, count(s), count(distinct s.beneficiario.id)
        from ServicioSocial s
        where (:inicio is null or s.fechaServicio >= :inicio)
          and (:fin is null or s.fechaServicio <= :fin)
        group by s.facultad
        """)
    List<Object[]> resumenPorFacultad(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
        select s.tipoServicio, count(s)
        from ServicioSocial s
        where (:inicio is null or s.fechaServicio >= :inicio)
          and (:fin is null or s.fechaServicio <= :fin)
        group by s.tipoServicio
        order by count(s) desc
        """)
    List<Object[]> problematicasFrecuentes(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<ServicioSocial> findByEstudianteIdOrderByFechaServicioDesc(String estudianteId);
}
