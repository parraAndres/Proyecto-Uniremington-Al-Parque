package com.vetsync.app.uniremington.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SyncBatchResponse {
    private UUID batchId;
    private String status;
    private Summary summary;
    private List<RecordResult> results;

    @Data
    @Builder
    public static class Summary {
        private int received;
        private int processed;
        private int duplicates;
        private int stale;
        private int errors;
    }

    @Data
    @Builder
    public static class RecordResult {
        private String idempotencyKey;
        private String status;
        private String message;
    }
}
