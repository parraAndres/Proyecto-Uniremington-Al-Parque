package com.vetsync.app.uniremington.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetsync.app.uniremington.dto.DomainRequests;
import com.vetsync.app.uniremington.dto.SyncBatchRequest;
import com.vetsync.app.uniremington.dto.SyncBatchResponse;
import com.vetsync.app.uniremington.entity.Beneficiario;
import com.vetsync.app.uniremington.entity.SyncBatch;
import com.vetsync.app.uniremington.entity.SyncEvent;
import com.vetsync.app.uniremington.repository.BeneficiarioRepository;
import com.vetsync.app.uniremington.repository.SyncBatchRepository;
import com.vetsync.app.uniremington.repository.SyncEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncBatchRepository syncBatchRepository;
    private final SyncEventRepository syncEventRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final DomainService domainService;
    private final ObjectMapper objectMapper;

    @Transactional
    public SyncBatchResponse sync(SyncBatchRequest request) {
        int processed = 0;
        int duplicates = 0;
        int stale = 0;
        int errors = 0;
        List<SyncBatchResponse.RecordResult> results = new ArrayList<>();

        for (SyncBatchRequest.Record record : request.getRecords()) {
            SyncBatchResponse.RecordResult.RecordResultBuilder result = SyncBatchResponse.RecordResult.builder()
                    .idempotencyKey(record.getIdempotencyKey());

            if (syncEventRepository.findByIdempotencyKey(record.getIdempotencyKey()).isPresent()) {
                duplicates++;
                results.add(result.status("DUPLICATE").message("Registro ya sincronizado").build());
                continue;
            }

            try {
                if (isStaleEvent(record)) {
                    stale++;
                    syncEventRepository.save(SyncEvent.builder()
                            .batchId(request.getBatchId())
                            .entityType(record.getEntityType())
                            .idempotencyKey(record.getIdempotencyKey())
                            .clientTimestamp(record.getClientTimestamp())
                            .status(SyncEvent.Estado.STALE_EVENT)
                            .errorDetail("Evento antiguo detectado por timestamp")
                            .build());
                    results.add(result.status("STALE_EVENT").message("Evento ignorado por timestamp").build());
                    continue;
                }

                processRecord(record);
                processed++;
                syncEventRepository.save(SyncEvent.builder()
                        .batchId(request.getBatchId())
                        .entityType(record.getEntityType())
                        .idempotencyKey(record.getIdempotencyKey())
                        .clientTimestamp(record.getClientTimestamp())
                        .appliedAt(LocalDateTime.now())
                        .status(SyncEvent.Estado.PROCESSED)
                        .build());
                results.add(result.status("PROCESSED").message("Registro aplicado").build());
            } catch (Exception ex) {
                errors++;
                syncEventRepository.save(SyncEvent.builder()
                        .batchId(request.getBatchId())
                        .entityType(record.getEntityType())
                        .idempotencyKey(record.getIdempotencyKey())
                        .clientTimestamp(record.getClientTimestamp())
                        .status(SyncEvent.Estado.ERROR)
                        .errorDetail(ex.getMessage())
                        .build());
                results.add(result.status("ERROR").message(ex.getMessage()).build());
            }
        }

        SyncBatch.Estado status = errors == 0
                ? SyncBatch.Estado.SUCCESS
                : (processed > 0 ? SyncBatch.Estado.PARTIAL_SUCCESS : SyncBatch.Estado.FAILED);

        syncBatchRepository.save(SyncBatch.builder()
                .batchId(request.getBatchId())
                .source(request.getSource())
                .submittedAt(request.getSubmittedAt())
                .status(status)
                .build());

        return SyncBatchResponse.builder()
                .batchId(request.getBatchId())
                .status(status.name())
                .summary(SyncBatchResponse.Summary.builder()
                        .received(request.getRecords().size())
                        .processed(processed)
                        .duplicates(duplicates)
                        .stale(stale)
                        .errors(errors)
                        .build())
                .results(results)
                .build();
    }

    private boolean isStaleEvent(SyncBatchRequest.Record record) {
        if (!"BENEFICIARIO".equalsIgnoreCase(record.getEntityType())) {
            return false;
        }
        DomainRequests.BeneficiarioRequest payload = objectMapper.convertValue(record.getPayload(), DomainRequests.BeneficiarioRequest.class);
        Beneficiario actual = beneficiarioRepository.findByDocumento(payload.getDocumento()).orElse(null);
        return actual != null && !record.getClientTimestamp().isAfter(actual.getUpdatedAt());
    }

    private void processRecord(SyncBatchRequest.Record record) {
        switch (record.getEntityType().toUpperCase()) {
            case "BENEFICIARIO" -> domainService.upsertBeneficiario(
                    objectMapper.convertValue(record.getPayload(), DomainRequests.BeneficiarioRequest.class));
            case "SERVICIO" -> domainService.createServicio(
                    objectMapper.convertValue(record.getPayload(), DomainRequests.ServicioRequest.class));
            case "SEGUIMIENTO" -> domainService.createSeguimiento(
                    objectMapper.convertValue(record.getPayload(), DomainRequests.SeguimientoRequest.class));
            case "ACADEMICO" -> domainService.createAcademico(
                    objectMapper.convertValue(record.getPayload(), DomainRequests.AcademicoRequest.class));
            case "RECURSO" -> domainService.createRecurso(
                    objectMapper.convertValue(record.getPayload(), DomainRequests.RecursoRequest.class));
            default -> throw new IllegalArgumentException("entityType no soportado: " + record.getEntityType());
        }
    }
}
