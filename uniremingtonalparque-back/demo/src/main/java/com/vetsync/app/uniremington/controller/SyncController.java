package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.SyncBatchRequest;
import com.vetsync.app.uniremington.dto.SyncBatchResponse;
import com.vetsync.app.uniremington.service.SyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("uniSyncLegacyController")
@RequestMapping("/uni/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping
    public ResponseEntity<SyncBatchResponse> sync(@Valid @RequestBody SyncBatchRequest request) {
        return ResponseEntity.ok(syncService.sync(request));
    }
}
