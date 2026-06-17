package com.example.mshistorial.Integration;

import com.example.mshistorial.client.MedicoClient;
import com.example.mshistorial.client.PacienteClient;
import com.example.mshistorial.dto.HistorialMedicoDto;
import com.example.mshistorial.dto.MedicoDTO;
import com.example.mshistorial.dto.PacienteDTO;
import com.example.mshistorial.model.HistorialMedico;
import com.example.mshistorial.repository.HistorialMedicoRepository;
import com.example.mshistorial.service.HistorialMedicoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HistorialMedicoIntegrationTest {

    @Autowired
    private HistorialMedicoService historialMedicoService;

    @Autowired
    private HistorialMedicoRepository historialMedicoRepository;

    @MockBean
    private PacienteClient pacienteClient;

    @MockBean
    private MedicoClient medicoClient;

    @Test
    void crearHistorial_DebePersistirEnBaseDatos() {

        PacienteDTO paciente = new PacienteDTO();
        paciente.setRut("11111111-1");

        MedicoDTO medico = new MedicoDTO();
        medico.setId(1L);

        when(pacienteClient.obtenerPacientePorRut("11111111-1"))
                .thenReturn(paciente);

        when(medicoClient.obtenerMedico(1L))
                .thenReturn(medico);

        HistorialMedicoDto dto = new HistorialMedicoDto();

        dto.setPacienteRut("11111111-1");
        dto.setMedicoId(1L);
        dto.setMedicoRut("22222222-2");
        dto.setDiagnostico("Gripe");
        dto.setEstado("ACTIVO");

        HistorialMedicoDto resultado = historialMedicoService.crearHistorialMedico(dto);

        assertNotNull(resultado);
        assertNotNull(resultado.getId());

        Optional<HistorialMedico> historial = historialMedicoRepository.findById(resultado.getId());

        assertTrue(historial.isPresent());

        assertEquals(
                "11111111-1",
                historial.get().getPacienteRut());

        assertEquals(
                "Gripe",
                historial.get().getDiagnostico());
    }

    @Test
    void obtenerHistorialPorId_DebeRetornarRegistroPersistido() {

        HistorialMedico historial = new HistorialMedico();

        historial.setPacienteRut("12345678-9");
        historial.setMedicoId(10L);
        historial.setMedicoRut("11111111-1");
        historial.setDiagnostico("Migraña");
        historial.setEstado("ACTIVO");

        HistorialMedico guardado = historialMedicoRepository.save(historial);

        HistorialMedicoDto resultado = historialMedicoService.obtenerHistorialMedicoPorId(
                guardado.getId());

        assertNotNull(resultado);

        assertEquals(
                guardado.getId(),
                resultado.getId());

        assertEquals(
                "Migraña",
                resultado.getDiagnostico());
    }

    @Test
    void eliminarHistorial_DebeEliminarRegistroRealmente() {

        HistorialMedico historial = new HistorialMedico();

        historial.setPacienteRut("12345678-9");
        historial.setMedicoId(10L);
        historial.setMedicoRut("11111111-1");
        historial.setDiagnostico("Prueba");
        historial.setEstado("ACTIVO");

        HistorialMedico guardado = historialMedicoRepository.save(historial);

        historialMedicoService.eliminarHistorial(
                guardado.getId());

        assertFalse(
                historialMedicoRepository.existsById(
                        guardado.getId()));
    }
}