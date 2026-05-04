package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.SyncEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncEventRepository extends JpaRepository<SyncEvent, Long> {
    Optional<SyncEvent> findByIdempotencyKey(String idempotencyKey);
}
