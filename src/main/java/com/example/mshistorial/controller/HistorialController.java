package com.example.mshistorial.controller;

import com.example.mshistorial.dto.HistorialMedicoDto;
import com.example.mshistorial.service.HistorialMedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialMedicoService historialMedicoService;

    // =========================
    // CREAR
    // =========================
    @PostMapping
    public ResponseEntity<HistorialMedicoDto> crearHistorial(
            @Valid @RequestBody HistorialMedicoDto dto) {

        HistorialMedicoDto nuevo = historialMedicoService.crearHistorialMedico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // =========================
    // GET POR PACIENTE (RUT)
    // =========================
    @GetMapping("/paciente/{pacienteRut}")
    public ResponseEntity<List<HistorialMedicoDto>> obtenerPorPaciente(
            @PathVariable String pacienteRut) {

        return ResponseEntity.ok(
                historialMedicoService.obtenerHistorialesPorPaciente(pacienteRut)
        );
    }

    // =========================
    // GET POR MEDICO
    // =========================
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<HistorialMedicoDto>> obtenerPorMedico(
            @PathVariable Long medicoId) {

        return ResponseEntity.ok(
                historialMedicoService.obtenerHistorialesPorMedico(medicoId)
        );
    }

    // =========================
    // GET POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<HistorialMedicoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                historialMedicoService.obtenerHistorialMedicoPorId(id)
        );
    }

    // =========================
    // GET TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<HistorialMedicoDto>> obtenerTodos() {
        return ResponseEntity.ok(
                historialMedicoService.obtenerTodos()
        );
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<HistorialMedicoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HistorialMedicoDto dto) {

        return ResponseEntity.ok(
                historialMedicoService.actualizarHistorial(id, dto)
        );
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        historialMedicoService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }
}   