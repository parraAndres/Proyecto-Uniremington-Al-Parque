package com.uniremington.alparque.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.alparque.dto.request.BeneficiarioSyncItemDTO;
import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionItemResultadoDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionLoteResumenDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;
import com.uniremington.alparque.service.SincronizacionService;

@WebMvcTest(SincronizacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SincronizacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SincronizacionService sincronizacionService;

    @Test
    void shouldAcceptBatchJsonAndReturnSummary() throws Exception {
        SincronizacionBatchRequestDTO request = new SincronizacionBatchRequestDTO();
        request.setLoteId("lote-json-1");
        request.setDispositivoId("tablet-001");

        BeneficiarioSyncItemDTO item = new BeneficiarioSyncItemDTO();
        item.setIdempotencyKey("idem-json-1");
        item.setClientRecordId("local-ben-1");
        item.setNombre("Ana Perez");
        item.setNumeroDocumento("123456789");
        item.setEdad(29);
        item.setGenero(Genero.FEMENINO);
        item.setTelefono("+573001112233");
        item.setMunicipio("Medellin");
        item.setBarrioVereda("Centro");
        item.setTipoPoblacion(TipoPoblacion.COMUNIDAD);
        item.setServicioSolicitado("Brigada juridica");
        item.setAutorizaDatos(true);
        request.setBeneficiarios(List.of(item));

        SincronizacionResponseDTO response = new SincronizacionResponseDTO();
        response.setLoteId("lote-json-1");
        response.setDispositivoId("tablet-001");
        response.setEstadoLote("COMPLETED");
        response.setMensaje("Lote procesado");
        response.setTotalRecibidos(1);
        response.setProcesados(1);
        response.setDuplicados(0);
        response.setConflictos(0);
        response.setErrores(0);
        response.setResultados(List.of(new SincronizacionItemResultadoDTO(
            "BENEFICIARIO",
            "idem-json-1",
            "local-ben-1",
            "CREATED",
            "server-id-1",
            "Beneficiario creado"
        )));

        when(sincronizacionService.sincronizarBatch(any(SincronizacionBatchRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/sincronizacion/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loteId").value("lote-json-1"))
            .andExpect(jsonPath("$.estadoLote").value("COMPLETED"))
            .andExpect(jsonPath("$.totalRecibidos").value(1))
            .andExpect(jsonPath("$.resultados[0].estado").value("CREATED"));
    }

    @Test
    void shouldReturnRecentLotesPage() throws Exception {
        SincronizacionLoteResumenDTO lote = new SincronizacionLoteResumenDTO(
            "lote-100",
            "tablet-001",
            "COMPLETED",
            15,
            15,
            0,
            0,
            0,
            null
        );

        SincronizacionLotePageResponseDTO response = new SincronizacionLotePageResponseDTO(
            List.of(lote),
            0,
            20,
            1,
            1
        );

        when(sincronizacionService.listarLotesRecientes(eq("tablet-001"), eq("COMPLETED"), eq(0), eq(20)))
            .thenReturn(response);

        mockMvc.perform(get("/api/sincronizacion/lotes/recientes")
                .param("dispositivoId", "tablet-001")
                .param("estadoLote", "COMPLETED")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].loteId").value("lote-100"))
            .andExpect(jsonPath("$.content[0].estadoLote").value("COMPLETED"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturnLotesHistoryByDateRange() throws Exception {
        SincronizacionLoteResumenDTO lote = new SincronizacionLoteResumenDTO(
            "lote-200",
            "tablet-001",
            "PARTIAL",
            10,
            8,
            1,
            1,
            0,
            null
        );

        SincronizacionLotePageResponseDTO response = new SincronizacionLotePageResponseDTO(
            List.of(lote),
            0,
            20,
            1,
            1
        );

        when(sincronizacionService.listarHistorialLotes(
            eq("tablet-001"),
            eq("PARTIAL"),
            eq(java.time.LocalDate.parse("2026-01-01")),
            eq(java.time.LocalDate.parse("2026-03-31")),
            eq(0),
            eq(20)))
            .thenReturn(response);

        mockMvc.perform(get("/api/sincronizacion/lotes/historial")
                .param("dispositivoId", "tablet-001")
                .param("estadoLote", "PARTIAL")
                .param("fechaInicio", "2026-01-01")
                .param("fechaFin", "2026-03-31")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].loteId").value("lote-200"))
            .andExpect(jsonPath("$.content[0].estadoLote").value("PARTIAL"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
}
