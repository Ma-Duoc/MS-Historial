package com.example.mshistorial.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.mshistorial.dto.PacienteDTO;

@FeignClient(name = "ms-pacientes", url = "http://localhost:8082")
public interface PacienteClient {

    @GetMapping("/api/pacientes/{rut}")
    PacienteDTO obtenerPacientePorRut(@PathVariable String rut);
}
