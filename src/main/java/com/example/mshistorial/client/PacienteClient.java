package com.example.mshistorial.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.mshistorial.dto.PacienteDTO;

@FeignClient(name = "ms-pacientes", url = "http://ms-pacientes:8082")
public interface PacienteClient {

    @GetMapping("/api/pacientes/{rut}")
    PacienteDTO obtenerPacientePorRut(@PathVariable String rut);
}