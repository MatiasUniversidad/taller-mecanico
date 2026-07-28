package com.example.signin.service;

import com.example.signin.dto.ReservaDTO;
import com.example.signin.model.Cliente;
import com.example.signin.model.Reserva;
import com.example.signin.model.Vehiculo;
import com.example.signin.repository.ClienteRepository;
import com.example.signin.repository.ReservaRepository;
import com.example.signin.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            VehiculoRepository vehiculoRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @Transactional
    public Reserva crearReserva(
            ReservaDTO reservaDTO
    ) {
        if (reservaDTO == null) {
            throw new IllegalArgumentException(
                    "Los datos de la reserva son obligatorios"
            );
        }

        if (reservaDTO.getClienteId() == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio"
            );
        }

        if (reservaDTO.getVehiculoId() == null) {
            throw new IllegalArgumentException(
                    "El vehículo es obligatorio"
            );
        }

        Cliente cliente =
                clienteRepository
                        .findById(
                                reservaDTO.getClienteId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Cliente no encontrado"
                                )
                        );

        Vehiculo vehiculo =
                vehiculoRepository
                        .findById(
                                reservaDTO.getVehiculoId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Vehículo no encontrado"
                                )
                        );

        /*
         * Evita crear una reserva asociando un vehículo
         * que pertenece a otro cliente.
         */
        if (
                vehiculo.getCliente() == null
                || !vehiculo.getCliente()
                        .getId()
                        .equals(cliente.getId())
        ) {
            throw new IllegalArgumentException(
                    "El vehículo seleccionado no pertenece al cliente"
            );
        }

        Reserva reserva = new Reserva();

        reserva.setFecha(
                reservaDTO.getFecha()
        );

        reserva.setHora(
                reservaDTO.getHora()
        );

        reserva.setMotivo(
                reservaDTO.getMotivo() == null
                        ? ""
                        : reservaDTO.getMotivo().trim()
        );

        reserva.setEstado(
                reservaDTO.getEstado() == null
                        || reservaDTO.getEstado().isBlank()
                        ? "PENDIENTE"
                        : reservaDTO.getEstado()
                                .trim()
                                .toUpperCase()
        );

        reserva.setCliente(cliente);
        reserva.setVehiculo(vehiculo);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminarReserva(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador de la reserva es obligatorio"
            );
        }

        Reserva reserva =
                reservaRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Reserva no encontrada"
                                )
                        );

        try {
            reservaRepository.delete(reserva);
            reservaRepository.flush();
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No se puede eliminar la reserva porque está asociada a otros registros. "
                            + "Elimine primero las dependencias relacionadas."
            );
        }
    }
}