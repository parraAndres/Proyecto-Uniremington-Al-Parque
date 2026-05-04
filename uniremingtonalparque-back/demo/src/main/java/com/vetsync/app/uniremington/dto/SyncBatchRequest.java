package com.vetsync.app.uniremington.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SyncBatchRequest {

    @NotNull
    private UUID batchId;

    @NotBlank
    private String source;

    @NotNull
    private LocalDateTime submittedAt;

    @NotEmpty
    @Valid
    private List<Record> records;

    @Data
    public static class Record {
        @NotBlank
        private String entityType;
        @NotBlank
        private String operation;
        @NotBlank
        private String idempotencyKey;
        @NotNull
        private LocalDateTime clientTimestamp;
        @NotNull
        private JsonNode payload;
    }
}
