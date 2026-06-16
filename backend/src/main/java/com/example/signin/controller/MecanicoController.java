package com.example.signin.controller;

import com.example.signin.model.Mecanico;
import com.example.signin.service.MecanicoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    private final MecanicoService mecanicoService;

    public MecanicoController(MecanicoService mecanicoService) {
        this.mecanicoService = mecanicoService;
    }

    @GetMapping
    public List<Mecanico> listarMecanicos() {
        return mecanicoService.listarMecanicos();
    }

    @GetMapping("/disponibles")
    public List<Mecanico> listarDisponibles() {
        return mecanicoService.listarDisponibles();
    }

    @GetMapping("/{id}")
    public Mecanico obtenerPorId(@PathVariable Integer id) {
        return mecanicoService.obtenerPorId(id);
    }

    @PostMapping
    public Mecanico crearMecanico(@RequestBody Mecanico mecanico) {
        return mecanicoService.crearMecanico(mecanico);
    }

    @PutMapping("/{id}")
    public Mecanico actualizarMecanico(
            @PathVariable Integer id,
            @RequestBody Mecanico mecanico
    ) {
        return mecanicoService.actualizarMecanico(id, mecanico);
    }

    @DeleteMapping("/{id}")
    public void eliminarMecanico(@PathVariable Integer id) {
        mecanicoService.eliminarMecanico(id);
    }
}