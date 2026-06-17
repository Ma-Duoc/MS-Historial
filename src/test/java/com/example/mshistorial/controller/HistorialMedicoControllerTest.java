package com.example.mshistorial.controller;

import com.example.mshistorial.controller.HistorialController;
import com.example.mshistorial.dto.HistorialMedicoDto;
import com.example.mshistorial.service.HistorialMedicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistorialController.class)
class HistorialMedicoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private HistorialMedicoService historialMedicoService;

        @Autowired
        private ObjectMapper objectMapper;

        private HistorialMedicoDto crearDto() {

                return new HistorialMedicoDto(
                                1L,
                                "12345678-9",
                                100L,
                                "11111111-1",
                                "Gripe",
                                "Reposo",
                                "PCR",
                                "ACTIVO",
                                LocalDateTime.now(),
                                LocalDateTime.now());
        }

        @Test
        void crearHistorial_DebeRetornar201() throws Exception {

                HistorialMedicoDto dto = crearDto();

                when(historialMedicoService.crearHistorialMedico(any()))
                                .thenReturn(dto);

                mockMvc.perform(post("/api/historial")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.pacienteRut").value("12345678-9"))
                                .andExpect(jsonPath("$.diagnostico").value("Gripe"));
        }

        @Test
        void obtenerPorPaciente_DebeRetornar200() throws Exception {

                HistorialMedicoDto dto = crearDto();

                when(historialMedicoService.obtenerHistorialesPorPaciente("12345678-9"))
                                .thenReturn(List.of(dto));

                mockMvc.perform(get("/api/historial/paciente/12345678-9"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].pacienteRut")
                                                .value("12345678-9"));
        }

        @Test
        void obtenerPorMedico_DebeRetornar200() throws Exception {

                HistorialMedicoDto dto = crearDto();

                when(historialMedicoService.obtenerHistorialesPorMedico(100L))
                                .thenReturn(List.of(dto));

                mockMvc.perform(get("/api/historial/medico/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].medicoId")
                                                .value(100));
        }

        @Test
        void obtenerPorId_DebeRetornar200() throws Exception {

                HistorialMedicoDto dto = crearDto();

                when(historialMedicoService.obtenerHistorialMedicoPorId(1L))
                                .thenReturn(dto);

                mockMvc.perform(get("/api/historial/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.diagnostico")
                                                .value("Gripe"));
        }

        @Test
        void obtenerTodos_DebeRetornar200() throws Exception {

                HistorialMedicoDto dto = crearDto();

                when(historialMedicoService.obtenerTodos())
                                .thenReturn(List.of(dto));

                mockMvc.perform(get("/api/historial"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id")
                                                .value(1));
        }

        @Test
        void actualizar_DebeRetornar200() throws Exception {

                HistorialMedicoDto dto = crearDto();
                dto.setDiagnostico("Actualizado");

                when(historialMedicoService.actualizarHistorial(
                                eq(1L),
                                any(HistorialMedicoDto.class))).thenReturn(dto);

                mockMvc.perform(put("/api/historial/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.diagnostico")
                                                .value("Actualizado"));
        }

        @Test
        void eliminar_DebeRetornar204() throws Exception {

                doNothing().when(historialMedicoService)
                                .eliminarHistorial(1L);

                mockMvc.perform(delete("/api/historial/1"))
                                .andExpect(status().isNoContent());
        }
}