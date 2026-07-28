package com.example.signin.service;

import com.example.signin.dto.PresupuestoDTO;
import com.example.signin.model.OrdenTrabajo;
import com.example.signin.model.Presupuesto;
import com.example.signin.model.SolicitudRepuesto;
import com.example.signin.repository.DocumentoPagoRepository;
import com.example.signin.repository.OrdenTrabajoRepository;
import com.example.signin.repository.PresupuestoRepository;
import com.example.signin.repository.SolicitudRepuestoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PresupuestoService {

    private static final String ESTADO_PENDIENTE =
            "PENDIENTE";

    private static final String ESTADO_ACEPTADO =
            "ACEPTADO";

    private static final String ESTADO_RECHAZADO =
            "RECHAZADO";

    private final PresupuestoRepository presupuestoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final SolicitudRepuestoRepository solicitudRepuestoRepository;
    private final DocumentoPagoRepository documentoPagoRepository;

    public PresupuestoService(
            PresupuestoRepository presupuestoRepository,
            OrdenTrabajoRepository ordenTrabajoRepository,
            SolicitudRepuestoRepository solicitudRepuestoRepository,
            DocumentoPagoRepository documentoPagoRepository
    ) {
        this.presupuestoRepository =
                presupuestoRepository;

        this.ordenTrabajoRepository =
                ordenTrabajoRepository;

        this.solicitudRepuestoRepository =
                solicitudRepuestoRepository;

        this.documentoPagoRepository =
                documentoPagoRepository;
    }

    public List<Presupuesto> listarPresupuestos() {
        return presupuestoRepository.findAll();
    }

    public Presupuesto obtenerPorId(Long id) {
        validarId(id);

        return presupuestoRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Presupuesto no encontrado"
                        )
                );
    }

    public Presupuesto obtenerPorOrden(
            Long ordenTrabajoId
    ) {
        if (ordenTrabajoId == null) {
            throw new IllegalArgumentException(
                    "El identificador de la orden es obligatorio"
            );
        }

        return presupuestoRepository
                .findByOrdenTrabajoId(
                        ordenTrabajoId
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Presupuesto no encontrado para esta orden"
                        )
                );
    }

    @Transactional
    public Presupuesto crearPresupuesto(
            PresupuestoDTO dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos del presupuesto son obligatorios"
            );
        }

        if (dto.getOrdenTrabajoId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una orden de trabajo"
            );
        }

        if (
                dto.getManoObra() != null
                && dto.getManoObra() < 0
        ) {
            throw new IllegalArgumentException(
                    "El monto de mano de obra no puede ser negativo"
            );
        }

        OrdenTrabajo orden =
                ordenTrabajoRepository
                        .findById(
                                dto.getOrdenTrabajoId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Orden de trabajo no encontrada"
                                )
                        );

        if (
                presupuestoRepository
                        .findByOrdenTrabajoId(
                                dto.getOrdenTrabajoId()
                        )
                        .isPresent()
        ) {
            throw new IllegalStateException(
                    "Esta orden ya tiene un presupuesto generado"
            );
        }

        List<SolicitudRepuesto> solicitudes =
                solicitudRepuestoRepository
                        .findByOrdenTrabajoId(
                                dto.getOrdenTrabajoId()
                        );

        double montoRepuestos = 0;

        for (
                SolicitudRepuesto solicitud
                : solicitudes
        ) {
            if (
                    solicitud.getRepuesto() == null
                    || solicitud
                            .getRepuesto()
                            .getPrecioUnitario() == null
            ) {
                continue;
            }

            int cantidad =
                    solicitud.getCantidad() != null
                            ? solicitud.getCantidad()
                            : 0;

            montoRepuestos +=
                    cantidad
                    * solicitud
                            .getRepuesto()
                            .getPrecioUnitario();
        }

        double manoObra =
                dto.getManoObra() != null
                        ? dto.getManoObra()
                        : 0;

        double montoTotal =
                montoRepuestos + manoObra;

        Presupuesto presupuesto =
                new Presupuesto();

        presupuesto.setOrdenTrabajo(orden);
        presupuesto.setMontoRepuestos(montoRepuestos);
        presupuesto.setManoObra(manoObra);
        presupuesto.setMontoTotal(montoTotal);
        presupuesto.setDetalle(
                limpiarTexto(dto.getDetalle())
        );
        presupuesto.setEstadoAprobacion(
                ESTADO_PENDIENTE
        );

        orden.setEstado("PRESUPUESTADA");
        ordenTrabajoRepository.save(orden);

        return presupuestoRepository.save(
                presupuesto
        );
    }

    /**
     * Mantiene el nombre aprobarPresupuesto para
     * no romper el controlador existente, pero el
     * estado guardado será ACEPTADO.
     */
    @Transactional
    public Presupuesto aprobarPresupuesto(Long id) {
        Presupuesto presupuesto =
                obtenerPorId(id);

        validarPresupuestoPendiente(
                presupuesto
        );

        presupuesto.setEstadoAprobacion(
                ESTADO_ACEPTADO
        );

        OrdenTrabajo orden =
                presupuesto.getOrdenTrabajo();

        if (orden == null) {
            throw new IllegalStateException(
                    "El presupuesto no tiene una orden de trabajo asociada"
            );
        }

        orden.setEstado(
                "PRESUPUESTO_ACEPTADO"
        );

        ordenTrabajoRepository.save(orden);

        return presupuestoRepository.save(
                presupuesto
        );
    }

    @Transactional
    public Presupuesto rechazarPresupuesto(Long id) {
        Presupuesto presupuesto =
                obtenerPorId(id);

        validarPresupuestoPendiente(
                presupuesto
        );

        presupuesto.setEstadoAprobacion(
                ESTADO_RECHAZADO
        );

        OrdenTrabajo orden =
                presupuesto.getOrdenTrabajo();

        if (orden == null) {
            throw new IllegalStateException(
                    "El presupuesto no tiene una orden de trabajo asociada"
            );
        }

        orden.setEstado(
                "PRESUPUESTO_RECHAZADO"
        );

        ordenTrabajoRepository.save(orden);

        return presupuestoRepository.save(
                presupuesto
        );
    }

    @Transactional
    public void eliminarPresupuesto(Long id) {
        Presupuesto presupuesto =
                obtenerPorId(id);

        if (
                documentoPagoRepository
                        .findByPresupuestoId(id)
                        .isPresent()
        ) {
            throw new IllegalStateException(
                    "No se puede eliminar este presupuesto porque ya tiene un documento de pago emitido. "
                    + "Elimine primero el documento de pago."
            );
        }

        if (
                ESTADO_ACEPTADO.equalsIgnoreCase(
                        presupuesto
                                .getEstadoAprobacion()
                )
        ) {
            throw new IllegalStateException(
                    "No se puede eliminar un presupuesto que ya fue aceptado"
            );
        }

        OrdenTrabajo orden =
                presupuesto.getOrdenTrabajo();

        presupuestoRepository.delete(
                presupuesto
        );

        presupuestoRepository.flush();

        /*
         * Si se elimina un presupuesto pendiente o
         * rechazado, la orden vuelve a un estado que
         * permite generar otro presupuesto.
         */
        if (orden != null) {
            orden.setEstado("EN_PROCESO");
            ordenTrabajoRepository.save(orden);
        }
    }

    private void validarPresupuestoPendiente(
            Presupuesto presupuesto
    ) {
        String estado =
                presupuesto.getEstadoAprobacion();

        if (
                estado == null
                || !ESTADO_PENDIENTE
                        .equalsIgnoreCase(estado)
        ) {
            throw new IllegalStateException(
                    "El presupuesto ya fue resuelto y no puede modificarse. Estado actual: "
                    + (
                        estado == null
                            ? "SIN ESTADO"
                            : estado
                    )
            );
        }
    }

    private void validarId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador del presupuesto es obligatorio"
            );
        }
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .trim()
                .replaceAll("\\s+", " ");
    }
}