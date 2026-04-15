package com.uniremington.alparque.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.SincronizacionLote;
import com.uniremington.alparque.model.SincronizacionResultadoItem;

public interface SincronizacionResultadoItemRepository extends JpaRepository<SincronizacionResultadoItem, UUID> {

    List<SincronizacionResultadoItem> findByLoteOrderByFechaRegistroAsc(SincronizacionLote lote);

    void deleteByLote(SincronizacionLote lote);
}