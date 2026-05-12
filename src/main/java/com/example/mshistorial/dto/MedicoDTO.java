package com.example.mshistorial.dto;

import lombok.Data;

@Data
public class MedicoDTO {

    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String email;
    private String telefono;
    private Boolean activo;
}
