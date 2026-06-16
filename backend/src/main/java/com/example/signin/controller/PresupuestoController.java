package com.example.signin.controller;

import com.example.signin.dto.PresupuestoDTO;
import com.example.signin.model.Presupuesto;
import com.example.signin.service.PresupuestoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping
    public List<Presupuesto> listarPresupuestos() {
        return presupuestoService.listarPresupuestos();
    }

    @GetMapping("/{id}")
    public Presupuesto obtenerPorId(@PathVariable("id") Long id) {
        return presupuestoService.obtenerPorId(id);
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public Presupuesto obtenerPorOrden(
            @PathVariable("ordenTrabajoId") Long ordenTrabajoId
    ) {
        return presupuestoService.obtenerPorOrden(ordenTrabajoId);
    }

    @PostMapping
    public Presupuesto crearPresupuesto(@RequestBody PresupuestoDTO dto) {
        return presupuestoService.crearPresupuesto(dto);
    }

    @PutMapping("/{id}/aprobar")
    public Presupuesto aprobarPresupuesto(@PathVariable("id") Long id) {
        return presupuestoService.aprobarPresupuesto(id);
    }

    @PutMapping("/{id}/rechazar")
    public Presupuesto rechazarPresupuesto(@PathVariable("id") Long id) {
        return presupuestoService.rechazarPresupuesto(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarPresupuesto(@PathVariable("id") Long id) {
        presupuestoService.eliminarPresupuesto(id);
    }
}