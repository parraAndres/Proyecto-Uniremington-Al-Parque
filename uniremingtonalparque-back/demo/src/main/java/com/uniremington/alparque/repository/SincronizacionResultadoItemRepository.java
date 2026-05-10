package com.uniremington.alparque.repository;

import com.uniremington.alparque.model.SincronizacionLote;
import com.uniremington.alparque.model.SincronizacionResultadoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SincronizacionResultadoItemRepository extends JpaRepository<SincronizacionResultadoItem, Long> {

    @Modifying
    @Transactional
    void deleteByLote(SincronizacionLote lote);
}
