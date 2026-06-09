package com.example.mshistorial.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.mshistorial.dto.MedicoDTO;

@FeignClient(name = "ms-medicos", url = "http://ms-medicos:8083")
public interface MedicoClient {

    @GetMapping("/api/medicos/{id}")
    MedicoDTO obtenerMedico(@PathVariable Long id);
}