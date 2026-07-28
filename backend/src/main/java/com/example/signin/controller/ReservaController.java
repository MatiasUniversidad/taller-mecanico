package com.example.signin.controller;

import com.example.signin.dto.ReservaDTO;
import com.example.signin.model.Reserva;
import com.example.signin.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(
            ReservaService reservaService
    ) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaService.listarReservas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(
            @RequestBody ReservaDTO reservaDTO
    ) {
        return reservaService.crearReserva(reservaDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarReserva(
            @PathVariable("id") Long id
    ) {
        reservaService.eliminarReserva(id);
    }
}