package com.example.signin.service;

import com.example.signin.model.Cliente;
import com.example.signin.model.Vehiculo;
import com.example.signin.repository.ClienteRepository;
import com.example.signin.repository.OrdenTrabajoRepository;
import com.example.signin.repository.ReservaRepository;
import com.example.signin.repository.VehiculoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ReservaRepository reservaRepository;

    public VehiculoService(
            VehiculoRepository vehiculoRepository,
            ClienteRepository clienteRepository,
            OrdenTrabajoRepository ordenTrabajoRepository,
            ReservaRepository reservaRepository
    ) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        validarDatosVehiculo(vehiculo);

        String patenteNormalizada =
                normalizarPatente(
                        vehiculo.getPatente()
                );

        validarPatenteDuplicada(
                patenteNormalizada,
                null
        );

        vehiculo.setPatente(
                patenteNormalizada
        );

        vehiculo.setMarca(
                limpiarTexto(
                        vehiculo.getMarca()
                )
        );

        vehiculo.setModelo(
                limpiarTexto(
                        vehiculo.getModelo()
                )
        );

        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo crearVehiculo(
            Integer clienteId,
            Vehiculo vehiculo
    ) {
        if (clienteId == null) {
            throw new IllegalArgumentException(
                    "El identificador del cliente es obligatorio"
            );
        }

        Cliente cliente =
                clienteRepository
                        .findById(clienteId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Cliente no encontrado"
                                )
                        );

        validarDatosVehiculo(vehiculo);

        String patenteNormalizada =
                normalizarPatente(
                        vehiculo.getPatente()
                );

        validarPatenteDuplicada(
                patenteNormalizada,
                null
        );

        vehiculo.setPatente(
                patenteNormalizada
        );

        vehiculo.setMarca(
                limpiarTexto(
                        vehiculo.getMarca()
                )
        );

        vehiculo.setModelo(
                limpiarTexto(
                        vehiculo.getModelo()
                )
        );

        vehiculo.setCliente(cliente);

        return vehiculoRepository.save(vehiculo);
    }

    public List<Vehiculo> obtenerVehiculosPorCliente(
            Integer clienteId
    ) {
        return vehiculoRepository.findByClienteId(
                clienteId
        );
    }

    public Vehiculo actualizarVehiculo(
            Integer id,
            Vehiculo vehiculoActualizado
    ) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador del vehículo es obligatorio"
            );
        }

        Vehiculo vehiculo =
                vehiculoRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Vehículo no encontrado"
                                )
                        );

        validarDatosActualizacion(
                vehiculoActualizado
        );

        /*
         * La patente no se modifica porque
         * identifica al vehículo.
         */
        vehiculo.setMarca(
                limpiarTexto(
                        vehiculoActualizado.getMarca()
                )
        );

        vehiculo.setModelo(
                limpiarTexto(
                        vehiculoActualizado.getModelo()
                )
        );

        vehiculo.setAnio(
                vehiculoActualizado.getAnio()
        );

        vehiculo.setKilometraje(
                vehiculoActualizado.getKilometraje()
        );

        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public void eliminarVehiculo(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador del vehículo es obligatorio"
            );
        }

        Vehiculo vehiculo =
                vehiculoRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Vehículo no encontrado"
                                )
                        );

        int cantidadReservas =
                reservaRepository
                        .findByVehiculoId(id)
                        .size();

        int cantidadOrdenes =
                ordenTrabajoRepository
                        .findByVehiculoId(id)
                        .size();

        List<String> dependencias =
                new ArrayList<>();

        if (cantidadReservas > 0) {
            dependencias.add(
                    cantidadReservas == 1
                            ? "1 reserva"
                            : cantidadReservas
                                    + " reservas"
            );
        }

        if (cantidadOrdenes > 0) {
            dependencias.add(
                    cantidadOrdenes == 1
                            ? "1 orden de trabajo"
                            : cantidadOrdenes
                                    + " órdenes de trabajo"
            );
        }

        if (!dependencias.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede eliminar el vehículo "
                            + vehiculo.getPatente()
                            + " porque tiene registros asociados: "
                            + String.join(
                                    ", ",
                                    dependencias
                            )
                            + ". Elimine primero estos registros."
            );
        }

        try {
            vehiculoRepository.delete(vehiculo);
            vehiculoRepository.flush();

        } catch (
                DataIntegrityViolationException exception
        ) {
            throw new IllegalStateException(
                    "No se puede eliminar el vehículo porque todavía tiene registros asociados."
            );
        }
    }

    private void validarDatosVehiculo(
            Vehiculo vehiculo
    ) {
        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "Los datos del vehículo son obligatorios"
            );
        }

        if (
                vehiculo.getPatente() == null
                || vehiculo.getPatente().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "La patente es obligatoria"
            );
        }

        validarDatosActualizacion(vehiculo);
    }

    private void validarDatosActualizacion(
            Vehiculo vehiculo
    ) {
        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "Los datos del vehículo son obligatorios"
            );
        }

        if (
                vehiculo.getMarca() == null
                || vehiculo.getMarca().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "La marca es obligatoria"
            );
        }

        if (
                vehiculo.getModelo() == null
                || vehiculo.getModelo().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "El modelo es obligatorio"
            );
        }

        if (
                vehiculo.getAnio() == null
                || vehiculo.getAnio() < 1900
                || vehiculo.getAnio() > 2100
        ) {
            throw new IllegalArgumentException(
                    "El año del vehículo no es válido"
            );
        }

        if (
                vehiculo.getKilometraje() == null
                || vehiculo.getKilometraje() < 0
        ) {
            throw new IllegalArgumentException(
                    "El kilometraje no puede ser negativo"
            );
        }
    }

    private void validarPatenteDuplicada(
            String patente,
            Integer vehiculoId
    ) {
        vehiculoRepository
                .findByPatente(patente)
                .filter(vehiculo ->
                        vehiculoId == null
                        || !vehiculo
                                .getId()
                                .equals(vehiculoId)
                )
                .ifPresent(vehiculo -> {
                    throw new IllegalArgumentException(
                            "Ya existe un vehículo registrado con esa patente"
                    );
                });
    }

    private String normalizarPatente(
            String patente
    ) {
        return patente == null
                ? ""
                : patente
                        .replaceAll(
                                "[^a-zA-Z0-9]",
                                ""
                        )
                        .trim()
                        .toUpperCase();
    }

    private String limpiarTexto(
            String texto
    ) {
        return texto == null
                ? ""
                : texto
                        .trim()
                        .replaceAll("\\s+", " ");
    }
}