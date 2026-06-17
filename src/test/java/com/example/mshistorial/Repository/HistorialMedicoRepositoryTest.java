package com.example.mshistorial.Repository;

import com.example.mshistorial.model.HistorialMedico;
import com.example.mshistorial.repository.HistorialMedicoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HistorialMedicoRepositoryTest {

    @Autowired
    private HistorialMedicoRepository repository;

    @Test
    void findByPacienteRut_DebeRetornarHistorialesDelPaciente() {

        HistorialMedico h = new HistorialMedico();
        h.setPacienteRut("11111111-1");
        h.setMedicoId(1L);
        h.setEstado("ACTIVO");
        h.setDiagnostico("Gripe");

        repository.save(h);

        List<HistorialMedico> resultado = repository.findByPacienteRut("11111111-1");

        assertEquals(1, resultado.size());
        assertEquals("11111111-1",
                resultado.get(0).getPacienteRut());
    }

    @Test
    void findByMedicoId_DebeRetornarHistorialesDelMedico() {

        HistorialMedico h = new HistorialMedico();
        h.setPacienteRut("11111111-1");
        h.setMedicoId(10L);
        h.setEstado("ACTIVO");

        repository.save(h);

        List<HistorialMedico> resultado = repository.findByMedicoId(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L,
                resultado.get(0).getMedicoId());
    }

    @Test
    void findByPacienteRutAndMedicoId_DebeRetornarCoincidencia() {

        HistorialMedico h = new HistorialMedico();
        h.setPacienteRut("11111111-1");
        h.setMedicoId(5L);
        h.setEstado("ACTIVO");

        repository.save(h);

        List<HistorialMedico> resultado = repository.findByPacienteRutAndMedicoId(
                "11111111-1",
                5L);

        assertEquals(1, resultado.size());
    }

    @Test
    void findHistorialesCompletosPorPaciente_DebeRetornarSoloHistorialesConContenido() {

        HistorialMedico h = new HistorialMedico();
        h.setPacienteRut("11111111-1");
        h.setMedicoId(1L);
        h.setDiagnostico("Gripe");
        h.setEstado("ACTIVO");

        repository.save(h);

        List<HistorialMedico> resultado = repository.findHistorialesCompletosPorPaciente(
                "11111111-1");

        assertEquals(1, resultado.size());
        assertEquals("Gripe",
                resultado.get(0).getDiagnostico());
    }
}