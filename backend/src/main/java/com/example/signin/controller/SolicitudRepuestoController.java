package com.example.signin.controller;

import com.example.signin.dto.SolicitudRepuestoDTO;
import com.example.signin.model.SolicitudRepuesto;
import com.example.signin.service.SolicitudRepuestoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-repuestos")
@CrossOrigin(origins = "*")
public class SolicitudRepuestoController {

    private final SolicitudRepuestoService solicitudRepuestoService;

    public SolicitudRepuestoController(SolicitudRepuestoService solicitudRepuestoService) {
        this.solicitudRepuestoService = solicitudRepuestoService;
    }

    @GetMapping
    public List<SolicitudRepuesto> listarSolicitudes() {
        return solicitudRepuestoService.listarSolicitudes();
    }

    @GetMapping("/{id}")
    public SolicitudRepuesto obtenerPorId(@PathVariable("id") Long id) {
        return solicitudRepuestoService.obtenerPorId(id);
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public List<SolicitudRepuesto> obtenerPorOrden(
            @PathVariable("ordenTrabajoId") Long ordenTrabajoId
    ) {
        return solicitudRepuestoService.obtenerPorOrden(ordenTrabajoId);
    }

    @GetMapping("/estado/{estado}")
    public List<SolicitudRepuesto> obtenerPorEstado(
            @PathVariable("estado") String estado
    ) {
        return solicitudRepuestoService.obtenerPorEstado(estado);
    }

    @PostMapping
    public SolicitudRepuesto crearSolicitud(@RequestBody SolicitudRepuestoDTO dto) {
        return solicitudRepuestoService.crearSolicitud(dto);
    }

    @DeleteMapping("/{id}")
    public void eliminarSolicitud(@PathVariable("id") Long id) {
        solicitudRepuestoService.eliminarSolicitud(id);
    }
}