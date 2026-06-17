package com.example.mshistorial.service;

import com.example.mshistorial.client.MedicoClient;
import com.example.mshistorial.client.PacienteClient;
import com.example.mshistorial.dto.HistorialMedicoDto;
import com.example.mshistorial.dto.MedicoDTO;
import com.example.mshistorial.dto.PacienteDTO;
import com.example.mshistorial.exception.ResourceNotFoundException;
import com.example.mshistorial.exception.ValidationException;
import com.example.mshistorial.model.HistorialMedico;
import com.example.mshistorial.repository.HistorialMedicoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialMedicoServiceTest {

    @Mock
    private HistorialMedicoRepository historialMedicoRepository;

    @Mock
    private PacienteClient pacienteClient;

    @Mock
    private MedicoClient medicoClient;

    @InjectMocks
    private HistorialMedicoService historialMedicoService;

    private HistorialMedico historial;
    private HistorialMedicoDto historialMedicoDto;
    private PacienteDTO pacienteDTO;
    private MedicoDTO medicoDTO;

    @BeforeEach
    void setUp() {

        historial = new HistorialMedico();
        historial.setId(1L);
        historial.setPacienteRut("12345678-9");
        historial.setMedicoId(100L);
        historial.setMedicoRut("11111111-1");
        historial.setDiagnostico("Gripe");
        historial.setTratamiento("Reposo");
        historial.setExamen("PCR");
        historial.setEstado("ACTIVO");

        historialMedicoDto = new HistorialMedicoDto();
        historialMedicoDto.setId(1L);
        historialMedicoDto.setPacienteRut("12345678-9");
        historialMedicoDto.setMedicoId(100L);
        historialMedicoDto.setMedicoRut("11111111-1");
        historialMedicoDto.setDiagnostico("Gripe");
        historialMedicoDto.setTratamiento("Reposo");
        historialMedicoDto.setExamen("PCR");
        historialMedicoDto.setEstado("ACTIVO");

        pacienteDTO = new PacienteDTO();
        pacienteDTO.setRut("12345678-9");
        pacienteDTO.setNombre("Juan");

        medicoDTO = new MedicoDTO();
        medicoDTO.setId(100L);
        medicoDTO.setRut("11111111-1");
        medicoDTO.setNombre("Pedro");
    }

    @Test
    void crearHistorialMedico_DebeLanzarResourceNotFoundException_PacienteNoExiste() {

        FeignException.NotFound notFound = mock(FeignException.NotFound.class);

        when(pacienteClient.obtenerPacientePorRut("12345678-9"))
                .thenThrow(notFound);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> historialMedicoService.crearHistorialMedico(historialMedicoDto));

        assertEquals(
                "Paciente no existe: 12345678-9",
                exception.getMessage());
    }

    @Test
    void crearHistorialMedico_DebeLanzarRuntimeException_CuandoPacienteServiceNoDisponible() {

        FeignException feignException = mock(FeignException.class);

        when(pacienteClient.obtenerPacientePorRut(anyString()))
                .thenThrow(feignException);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> historialMedicoService.crearHistorialMedico(historialMedicoDto));

        assertEquals(
                "MS_PACIENTES_UNAVAILABLE",
                exception.getMessage());
    }

    @Test
    void crearHistorialMedico_DebeLanzarRuntimeException_CuandoMedicoServiceNoDisponible() {

        when(pacienteClient.obtenerPacientePorRut(anyString()))
                .thenReturn(pacienteDTO);

        FeignException feignException = mock(FeignException.class);

        when(medicoClient.obtenerMedico(anyLong()))
                .thenThrow(feignException);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> historialMedicoService.crearHistorialMedico(historialMedicoDto));

        assertEquals(
                "MS_MEDICOS_UNAVAILABLE",
                exception.getMessage());
    }

    @Test
    void obtenerHistorialMedicoPorId_DebeLanzarResourceNotFoundException() {

        when(historialMedicoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> historialMedicoService.obtenerHistorialMedicoPorId(999L));

        assertEquals(
                "Historial no encontrado: 999",
                exception.getMessage());
    }

    @Test
    void obtenerHistorialesPorPaciente_DebeRetornarLista() {

        when(historialMedicoRepository.findByPacienteRut("12345678-9"))
                .thenReturn(List.of(historial));

        List<HistorialMedicoDto> resultado = historialMedicoService.obtenerHistorialesPorPaciente("12345678-9");

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerHistorialesPorMedico_DebeRetornarLista() {

        when(historialMedicoRepository.findByMedicoId(100L))
                .thenReturn(List.of(historial));

        List<HistorialMedicoDto> resultado = historialMedicoService.obtenerHistorialesPorMedico(100L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerTodos_DebeRetornarLista() {

        when(historialMedicoRepository.findAll())
                .thenReturn(List.of(historial));

        List<HistorialMedicoDto> resultado = historialMedicoService.obtenerTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void actualizarHistorial_DebeLanzarResourceNotFoundException() {

        when(historialMedicoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> historialMedicoService.actualizarHistorial(
                        999L,
                        historialMedicoDto));

        assertEquals(
                "Historial no encontrado: 999",
                exception.getMessage());
    }

    @Test
    void eliminarHistorial_DebeLanzarResourceNotFoundException() {

        when(historialMedicoRepository.existsById(999L))
                .thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> historialMedicoService.eliminarHistorial(999L));

        assertEquals(
                "Historial no encontrado: 999",
                exception.getMessage());
    }

    @Test
    void fallbackGeneral_DebeRelanzarResourceNotFoundException() {

        ResourceNotFoundException original = new ResourceNotFoundException("Error");

        assertThrows(
                ResourceNotFoundException.class,
                () -> historialMedicoService.fallbackGeneral(
                        historialMedicoDto,
                        original));
    }

    @Test
    void fallbackGeneral_DebeRelanzarValidationException() {

        ValidationException original = new ValidationException("Error");

        assertThrows(
                ValidationException.class,
                () -> historialMedicoService.fallbackGeneral(
                        historialMedicoDto,
                        original));
    }

    @Test
    void fallbackGeneral_DebeLanzarRuntimeException_PacienteServiceNoDisponible() {

        RuntimeException original = new RuntimeException("MS_PACIENTES_UNAVAILABLE");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> historialMedicoService.fallbackGeneral(
                        historialMedicoDto,
                        original));

        assertEquals(
                "Servicio de pacientes no disponible",
                exception.getMessage());
    }

    @Test
    void fallbackGeneral_DebeLanzarRuntimeException_MedicoServiceNoDisponible() {

        RuntimeException original = new RuntimeException("MS_MEDICOS_UNAVAILABLE");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> historialMedicoService.fallbackGeneral(
                        historialMedicoDto,
                        original));

        assertEquals(
                "Servicio de médicos no disponible",
                exception.getMessage());
    }

    @Test
    void fallbackGeneral_DebeLanzarRuntimeException_General() {

        RuntimeException original = new RuntimeException("Error cualquiera");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> historialMedicoService.fallbackGeneral(
                        historialMedicoDto,
                        original));

        assertEquals(
                "Servicios externos no disponibles (Paciente/Medico)",
                exception.getMessage());
    }
}