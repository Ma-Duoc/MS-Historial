package com.example.mshistorial.service;

import com.example.mshistorial.client.MedicoClient;
import com.example.mshistorial.client.PacienteClient;
import com.example.mshistorial.dto.HistorialMedicoDto;
import com.example.mshistorial.exception.ResourceNotFoundException;
import com.example.mshistorial.exception.ValidationException;
import com.example.mshistorial.model.HistorialMedico;
import com.example.mshistorial.repository.HistorialMedicoRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialMedicoService {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    // =========================
    // CREAR HISTORIAL
    // =========================
    @Transactional
    @CircuitBreaker(name = "historialService", fallbackMethod = "fallbackGeneral")
    public HistorialMedicoDto crearHistorialMedico(HistorialMedicoDto dto) {

        validarCamposObligatorios(dto);

        validarPaciente(dto);
        validarMedico(dto);

        HistorialMedico historial = convertToEntity(dto);
        HistorialMedico saved = historialMedicoRepository.save(historial);

        return convertToDto(saved);
    }

    // =========================
    // VALIDAR PACIENTE
    // =========================
    private void validarPaciente(HistorialMedicoDto dto) {
        try {
            pacienteClient.obtenerPacientePorRut(dto.getPacienteRut());

        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Paciente no existe: " + dto.getPacienteRut());

        } catch (FeignException e) {
            throw new RuntimeException("MS_PACIENTES_UNAVAILABLE");
        }
    }

    // =========================
    // VALIDAR MÉDICO
    // =========================
    private void validarMedico(HistorialMedicoDto dto) {
        try {
            medicoClient.obtenerMedico(dto.getMedicoId());

        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Médico no existe: " + dto.getMedicoId());

        } catch (FeignException e) {
            throw new RuntimeException("MS_MEDICOS_UNAVAILABLE");
        }
    }

    // =========================
    // CONSULTAS
    // =========================
    public HistorialMedicoDto obtenerHistorialMedicoPorId(Long id) {
        HistorialMedico historial = historialMedicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial no encontrado: " + id));

        return convertToDto(historial);
    }

    public List<HistorialMedicoDto> obtenerHistorialesPorPaciente(String pacienteRut) {
        return historialMedicoRepository.findByPacienteRut(pacienteRut)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<HistorialMedicoDto> obtenerHistorialesPorMedico(Long medicoId) {
        return historialMedicoRepository.findByMedicoId(medicoId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<HistorialMedicoDto> obtenerTodos() {
        return historialMedicoRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @Transactional
    public HistorialMedicoDto actualizarHistorial(Long id, HistorialMedicoDto dto) {

        validarCamposObligatorios(dto);

        HistorialMedico existente = historialMedicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial no encontrado: " + id));

        validarPaciente(dto);
        validarMedico(dto);

        existente.setDiagnostico(dto.getDiagnostico());
        existente.setTratamiento(dto.getTratamiento());
        existente.setExamen(dto.getExamen());

        return convertToDto(historialMedicoRepository.save(existente));
    }

    // =========================
    // ELIMINAR
    // =========================
    @Transactional
    public void eliminarHistorial(Long id) {
        if (!historialMedicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Historial no encontrado: " + id);
        }
        historialMedicoRepository.deleteById(id);
    }

    // =========================
    // VALIDACIONES
    // =========================
    private void validarCamposObligatorios(HistorialMedicoDto dto) {

        boolean tieneDiagnostico = dto.getDiagnostico() != null && !dto.getDiagnostico().isBlank();
        boolean tieneTratamiento = dto.getTratamiento() != null && !dto.getTratamiento().isBlank();
        boolean tieneExamen = dto.getExamen() != null && !dto.getExamen().isBlank();

        if (!tieneDiagnostico && !tieneTratamiento && !tieneExamen) {
            throw new ValidationException("Debe ingresar diagnóstico, tratamiento o examen");
        }
    }

    // =========================
    // MAPPERS
    // =========================
    private HistorialMedicoDto convertToDto(HistorialMedico h) {
        return new HistorialMedicoDto(
                h.getId(),
                h.getPacienteRut(),
                h.getMedicoId(),
                h.getMedicoRut(),
                h.getDiagnostico(),
                h.getTratamiento(),
                h.getExamen(),
                h.getFechaCreacion(),
                h.getFechaActualizacion()
        );
    }

    private HistorialMedico convertToEntity(HistorialMedicoDto dto) {
        HistorialMedico h = new HistorialMedico();
        h.setPacienteRut(dto.getPacienteRut());
        h.setMedicoId(dto.getMedicoId());
        h.setMedicoRut(dto.getMedicoRut());
        h.setDiagnostico(dto.getDiagnostico());
        h.setTratamiento(dto.getTratamiento());
        h.setExamen(dto.getExamen());
        return h;
    }

    // =========================
    // CIRCUIT BREAKER FALLBACK (CLAVE 🔥)
    // =========================
    public HistorialMedicoDto fallbackGeneral(HistorialMedicoDto dto, Throwable t) {

        // 👉 Si es error de negocio, lo devolvemos tal cual
        if (t instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) t;
        }

        if (t instanceof ValidationException) {
            throw (ValidationException) t;
        }

        // 👉 Si viene de microservicio caído
        if (t.getMessage() != null && t.getMessage().contains("MS_PACIENTES_UNAVAILABLE")) {
            throw new RuntimeException("Servicio de pacientes no disponible");
        }

        if (t.getMessage() != null && t.getMessage().contains("MS_MEDICOS_UNAVAILABLE")) {
            throw new RuntimeException("Servicio de médicos no disponible");
        }

        // 👉 fallback general
        throw new RuntimeException("Servicios externos no disponibles (Paciente/Medico)");
    }
}
