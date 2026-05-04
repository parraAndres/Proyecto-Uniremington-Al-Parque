package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sync_events", indexes = {
        @Index(name = "idx_sync_event_batch", columnList = "batchId"),
        @Index(name = "idx_sync_event_entity", columnList = "entityType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncEvent {

    public enum Estado { PROCESSED, DUPLICATE, STALE_EVENT, ERROR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID batchId;

    @Column(nullable = false, length = 40)
    private String entityType;

    @Column(nullable = false, length = 120, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDateTime clientTimestamp;

    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado status;

    @Column(columnDefinition = "TEXT")
    private String errorDetail;
}
