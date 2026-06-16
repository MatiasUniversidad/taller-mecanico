package com.example.signin.controller;

import com.example.signin.dto.DiagnosticoDTO;
import com.example.signin.model.Diagnostico;
import com.example.signin.service.DiagnosticoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosticos")
@CrossOrigin(origins = "*")
public class DiagnosticoController {

    private final DiagnosticoService diagnosticoService;

    public DiagnosticoController(DiagnosticoService diagnosticoService) {
        this.diagnosticoService = diagnosticoService;
    }

    @GetMapping
    public List<Diagnostico> listarDiagnosticos() {
        return diagnosticoService.listarDiagnosticos();
    }

    @GetMapping("/{id}")
    public Diagnostico obtenerPorId(@PathVariable("id") Long id) {
        return diagnosticoService.obtenerPorId(id);
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public Diagnostico obtenerPorOrden(
            @PathVariable("ordenTrabajoId") Long ordenTrabajoId
    ) {
        return diagnosticoService.obtenerPorOrden(ordenTrabajoId);
    }

    @PostMapping
    public Diagnostico crearDiagnostico(@RequestBody DiagnosticoDTO dto) {
        return diagnosticoService.crearDiagnostico(dto);
    }

    @PutMapping("/{id}")
    public Diagnostico actualizarDiagnostico(
            @PathVariable("id") Long id,
            @RequestBody DiagnosticoDTO dto
    ) {
        return diagnosticoService.actualizarDiagnostico(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminarDiagnostico(@PathVariable("id") Long id) {
        diagnosticoService.eliminarDiagnostico(id);
    }
}