package com.example.mshistorial.repository;

import com.example.mshistorial.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    // 🔥 CAMBIO CLAVE
    List<HistorialMedico> findByPacienteRut(String pacienteRut);

    List<HistorialMedico> findByMedicoId(Long medicoId);

    List<HistorialMedico> findByDiagnosticoIsNotNull();

    List<HistorialMedico> findByTratamientoIsNotNull();

    List<HistorialMedico> findByExamenIsNotNull();

    // 🔥 QUERY CORREGIDA
    @Query("SELECT h FROM HistorialMedico h WHERE h.pacienteRut = :pacienteRut AND h.medicoId = :medicoId")
    List<HistorialMedico> findByPacienteRutAndMedicoId(
            @Param("pacienteRut") String pacienteRut,
            @Param("medicoId") Long medicoId
    );

    // 🔥 QUERY IMPORTANTE PARA TU CASO
    @Query("""
        SELECT h FROM HistorialMedico h 
        WHERE h.pacienteRut = :pacienteRut 
        AND (h.diagnostico IS NOT NULL 
        OR h.tratamiento IS NOT NULL 
        OR h.examen IS NOT NULL)
    """)
    List<HistorialMedico> findHistorialesCompletosPorPaciente(
            @Param("pacienteRut") String pacienteRut
    );
}