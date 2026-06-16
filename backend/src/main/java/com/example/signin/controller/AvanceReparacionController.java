package com.example.signin.controller;

import com.example.signin.dto.AvanceReparacionDTO;
import com.example.signin.model.AvanceReparacion;
import com.example.signin.service.AvanceReparacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avances")
@CrossOrigin(origins = "*")
public class AvanceReparacionController {

    private final AvanceReparacionService avanceReparacionService;

    public AvanceReparacionController(AvanceReparacionService avanceReparacionService) {
        this.avanceReparacionService = avanceReparacionService;
    }

    @GetMapping
    public List<AvanceReparacion> listarAvances() {
        return avanceReparacionService.listarAvances();
    }

    @GetMapping("/{id}")
    public AvanceReparacion obtenerPorId(@PathVariable("id") Long id) {
        return avanceReparacionService.obtenerPorId(id);
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public List<AvanceReparacion> obtenerPorOrden(
            @PathVariable("ordenTrabajoId") Long ordenTrabajoId
    ) {
        return avanceReparacionService.obtenerPorOrden(ordenTrabajoId);
    }

    @GetMapping("/estado/{estado}")
    public List<AvanceReparacion> obtenerPorEstado(
            @PathVariable("estado") String estado
    ) {
        return avanceReparacionService.obtenerPorEstado(estado);
    }

    @PostMapping
    public AvanceReparacion crearAvance(@RequestBody AvanceReparacionDTO dto) {
        return avanceReparacionService.crearAvance(dto);
    }

    @PutMapping("/{id}")
    public AvanceReparacion actualizarAvance(
            @PathVariable("id") Long id,
            @RequestBody AvanceReparacionDTO dto
    ) {
        return avanceReparacionService.actualizarAvance(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminarAvance(@PathVariable("id") Long id) {
        avanceReparacionService.eliminarAvance(id);
    }
}