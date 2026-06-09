package com.example.mshistorial.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historiales_medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_rut", nullable = false)
    private String pacienteRut;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "medico_rut")
    private String medicoRut;

    @Column(name = "diagnostico", length = 2000)
    private String diagnostico;

    @Column(name = "tratamiento", length = 2000)
    private String tratamiento;

    @Column(name = "examen", length = 2000)
    private String examen;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "Estado", nullable = false)
    private String estado;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}