package com.uniremington.alparque.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uniremington.alparque.dto.request.BeneficiarioSyncItemDTO;
import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.model.Beneficiario;
import com.uniremington.alparque.model.SincronizacionEvento;
import com.uniremington.alparque.model.SincronizacionLote;
import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.repository.ServicioRepository;
import com.uniremington.alparque.repository.SincronizacionEventoRepository;
import com.uniremington.alparque.repository.SincronizacionLoteRepository;
import com.uniremington.alparque.repository.SincronizacionResultadoItemRepository;

@ExtendWith(MockitoExtension.class)
class SincronizacionServiceImplTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private SincronizacionEventoRepository sincronizacionEventoRepository;

    @Mock
    private SincronizacionLoteRepository sincronizacionLoteRepository;

    @Mock
    private SincronizacionResultadoItemRepository sincronizacionResultadoItemRepository;

    @InjectMocks
    private SincronizacionServiceImpl sincronizacionService;

    @Test
    void shouldReturnDuplicateWhenIdempotencyKeyAlreadyExists() {
        SincronizacionEvento evento = new SincronizacionEvento();
        evento.setEntidadId(UUID.randomUUID());

        when(sincronizacionLoteRepository.save(any(SincronizacionLote.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(sincronizacionResultadoItemRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        when(sincronizacionLoteRepository.findByLoteIdAndDispositivoId("lote-1", "device-1"))
            .thenReturn(Optional.empty());
        when(sincronizacionEventoRepository.findByIdempotencyKey("idem-1"))
            .thenReturn(Optional.of(evento));
        doNothing().when(sincronizacionResultadoItemRepository).deleteByLote(any());

        SincronizacionBatchRequestDTO request = new SincronizacionBatchRequestDTO();
        request.setLoteId("lote-1");
        request.setDispositivoId("device-1");
        request.setBeneficiarios(List.of(buildBeneficiario("idem-1", LocalDateTime.now())));

        SincronizacionResponseDTO response = sincronizacionService.sincronizarBatch(request);

        assertEquals(1, response.getTotalRecibidos());
        assertEquals(1, response.getDuplicados());
        assertEquals(0, response.getProcesados());
        assertEquals("COMPLETED", response.getEstadoLote());
    }

    @Test
    void shouldIgnoreStaleBeneficiarioEventByClientUpdatedAt() {
        UUID beneficiarioId = UUID.randomUUID();

        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(beneficiarioId);

        when(sincronizacionLoteRepository.save(any(SincronizacionLote.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(sincronizacionResultadoItemRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SincronizacionEvento eventoReciente = new SincronizacionEvento();
        eventoReciente.setFechaCliente(LocalDateTime.now().minusHours(1));

        when(sincronizacionLoteRepository.findByLoteIdAndDispositivoId("lote-2", "device-2"))
            .thenReturn(Optional.empty());
        when(sincronizacionEventoRepository.findByIdempotencyKey("idem-2"))
            .thenReturn(Optional.empty());
        when(beneficiarioRepository.findByNumeroDocumento("123456"))
            .thenReturn(Optional.of(beneficiario));
        when(sincronizacionEventoRepository.findTopByTipoEntidadAndEntidadIdOrderByFechaClienteDescFechaRegistroDesc("BENEFICIARIO", beneficiarioId))
            .thenReturn(Optional.of(eventoReciente));
        doNothing().when(sincronizacionResultadoItemRepository).deleteByLote(any());

        SincronizacionBatchRequestDTO request = new SincronizacionBatchRequestDTO();
        request.setLoteId("lote-2");
        request.setDispositivoId("device-2");
        request.setBeneficiarios(List.of(buildBeneficiario("idem-2", LocalDateTime.now().minusDays(1))));

        SincronizacionResponseDTO response = sincronizacionService.sincronizarBatch(request);

        assertEquals(1, response.getTotalRecibidos());
        assertEquals(1, response.getConflictos());
        assertEquals(0, response.getProcesados());
        assertEquals("PARTIAL", response.getEstadoLote());

        verify(beneficiarioRepository).findByNumeroDocumento("123456");
    }

    private BeneficiarioSyncItemDTO buildBeneficiario(String idempotencyKey, LocalDateTime clientUpdatedAt) {
        BeneficiarioSyncItemDTO item = new BeneficiarioSyncItemDTO();
        item.setIdempotencyKey(idempotencyKey);
        item.setClientRecordId("local-1");
        item.setClientUpdatedAt(clientUpdatedAt);
        item.setNombre("Ana");
        item.setNumeroDocumento("123456");
        item.setEdad(25);
        item.setGenero(Genero.FEMENINO);
        item.setTelefono("+573001112233");
        item.setMunicipio("Medellin");
        item.setBarrioVereda("Centro");
        item.setTipoPoblacion(TipoPoblacion.COMUNIDAD);
        item.setServicioSolicitado("Asesoria");
        item.setAutorizaDatos(true);
        return item;
    }
}