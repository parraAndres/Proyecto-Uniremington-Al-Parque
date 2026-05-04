package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.SyncBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SyncBatchRepository extends JpaRepository<SyncBatch, Long> {
    Optional<SyncBatch> findByBatchId(UUID batchId);
}
