package com.example.mshistorial.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedicoDto {

    private Long id;

    @NotNull(message = "El RUT del paciente es obligatorio")
    private String pacienteRut;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    private String medicoRut;

    private String diagnostico;
    private String tratamiento;
    private String examen;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
