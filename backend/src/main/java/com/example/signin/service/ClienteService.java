package com.example.signin.service;

import com.example.signin.model.Cliente;
import com.example.signin.model.Vehiculo;
import com.example.signin.repository.ClienteRepository;
import com.example.signin.repository.OrdenTrabajoRepository;
import com.example.signin.repository.ReservaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ReservaRepository reservaRepository;
    private final ValidacionService validacionService;

    public ClienteService(
            ClienteRepository clienteRepository,
            OrdenTrabajoRepository ordenTrabajoRepository,
            ReservaRepository reservaRepository,
            ValidacionService validacionService
    ) {
        this.clienteRepository = clienteRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.reservaRepository = reservaRepository;
        this.validacionService = validacionService;
    }

    public Cliente crearCliente(Cliente cliente) {
        validarDatosCliente(cliente);

        String rutFormateado =
                validacionService.formatearRut(
                        cliente.getRut()
                );

        if (
                clienteRepository
                        .findByRut(rutFormateado)
                        .isPresent()
        ) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese RUT"
            );
        }

        String emailNormalizado =
                cliente.getEmail()
                        .trim()
                        .toLowerCase();

        if (
                clienteRepository
                        .findByEmail(emailNormalizado)
                        .isPresent()
        ) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese email"
            );
        }

        cliente.setRut(rutFormateado);

        cliente.setNombre(
                limpiarEspacios(
                        cliente.getNombre()
                )
        );

        cliente.setTelefono(
                limpiarTexto(
                        cliente.getTelefono()
                )
        );

        cliente.setEmail(emailNormalizado);

        cliente.setDireccion(
                limpiarTexto(
                        cliente.getDireccion()
                )
        );

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> obtenerClientePorId(
            Integer id
    ) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> obtenerTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente actualizarCliente(
            Integer id,
            Cliente clienteActualizado
    ) {
        Cliente cliente =
                clienteRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Cliente no encontrado"
                                )
                        );

        validarDatosCliente(
                clienteActualizado
        );

        String emailNormalizado =
                clienteActualizado
                        .getEmail()
                        .trim()
                        .toLowerCase();

        clienteRepository
                .findByEmail(emailNormalizado)
                .filter(clienteEncontrado ->
                        !clienteEncontrado
                                .getId()
                                .equals(id)
                )
                .ifPresent(clienteEncontrado -> {
                    throw new IllegalArgumentException(
                            "Ya existe otro cliente con ese email"
                    );
                });

        cliente.setNombre(
                limpiarEspacios(
                        clienteActualizado.getNombre()
                )
        );

        cliente.setTelefono(
                limpiarTexto(
                        clienteActualizado.getTelefono()
                )
        );

        cliente.setEmail(emailNormalizado);

        cliente.setDireccion(
                limpiarTexto(
                        clienteActualizado.getDireccion()
                )
        );

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador del cliente es obligatorio"
            );
        }

        Cliente cliente =
                clienteRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Cliente no encontrado"
                                )
                        );

        List<Vehiculo> vehiculos =
                cliente.getVehiculos() == null
                        ? new ArrayList<>()
                        : cliente.getVehiculos();

        int cantidadVehiculos =
                vehiculos.size();

        int cantidadReservas =
                reservaRepository
                        .findByClienteId(id)
                        .size();

        int cantidadOrdenes = 0;

        /*
         * Las órdenes están asociadas a los vehículos.
         * Se revisan todos los vehículos del cliente.
         */
        for (Vehiculo vehiculo : vehiculos) {
            cantidadOrdenes +=
                    ordenTrabajoRepository
                            .findByVehiculoId(
                                    vehiculo.getId()
                            )
                            .size();
        }

        List<String> dependencias =
                new ArrayList<>();

        if (cantidadVehiculos > 0) {
            dependencias.add(
                    cantidadVehiculos == 1
                            ? "1 vehículo"
                            : cantidadVehiculos
                                    + " vehículos"
            );
        }

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
                    "No se puede eliminar al cliente "
                            + cliente.getNombre()
                            + " porque tiene registros asociados: "
                            + String.join(
                                    ", ",
                                    dependencias
                            )
                            + ". Elimine primero estos registros."
            );
        }

        try {
            clienteRepository.delete(cliente);
            clienteRepository.flush();
        } catch (
                DataIntegrityViolationException exception
        ) {
            throw new IllegalStateException(
                    "No se puede eliminar al cliente porque todavía tiene registros asociados."
            );
        }
    }

    public Optional<Cliente> obtenerClientePorRut(
            String rut
    ) {
        return clienteRepository.findByRut(rut);
    }

    public Optional<Cliente> obtenerClientePorEmail(
            String email
    ) {
        return clienteRepository.findByEmail(email);
    }

    private void validarDatosCliente(
            Cliente cliente
    ) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Los datos del cliente son obligatorios"
            );
        }

        if (
                !validacionService.esRutValido(
                        cliente.getRut()
                )
        ) {
            throw new IllegalArgumentException(
                    "El RUT ingresado no es válido"
            );
        }

        if (
                !validacionService
                        .esNombreCompletoValido(
                                cliente.getNombre()
                        )
        ) {
            throw new IllegalArgumentException(
                    "Debe ingresar el nombre completo del cliente"
            );
        }

        if (
                !validacionService
                        .esTelefonoChilenoValido(
                                cliente.getTelefono()
                        )
        ) {
            throw new IllegalArgumentException(
                    "El teléfono debe tener el formato +56912345678"
            );
        }

        if (
                cliente.getEmail() == null
                || cliente.getEmail().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "El correo electrónico es obligatorio"
            );
        }

        if (
                !validacionService.esEmailValido(
                        cliente.getEmail()
                )
        ) {
            throw new IllegalArgumentException(
                    "El correo electrónico no tiene un formato válido"
            );
        }
    }

    private String limpiarEspacios(
            String texto
    ) {
        if (texto == null) {
            return "";
        }

        return texto
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String limpiarTexto(
            String texto
    ) {
        return texto == null
                ? ""
                : texto.trim();
    }
}