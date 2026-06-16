package com.example.signin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "presupuesto")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto_repuestos", nullable = false)
    private Double montoRepuestos;

    @Column(name = "mano_obra", nullable = false)
    private Double manoObra;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @Column(name = "estado_aprobacion", nullable = false, length = 50)
    private String estadoAprobacion;

    @Column(length = 1000)
    private String detalle;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @OneToOne
    @JoinColumn(name = "orden_trabajo_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"vehiculo", "mecanico"})
    private OrdenTrabajo ordenTrabajo;

    @PrePersist
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();

        if (this.estadoAprobacion == null || this.estadoAprobacion.isBlank()) {
            this.estadoAprobacion = "PENDIENTE";
        }
    }

    public Presupuesto() {}

    public Long getId() { return id; }
    public Double getMontoRepuestos() { return montoRepuestos; }
    public Double getManoObra() { return manoObra; }
    public Double getMontoTotal() { return montoTotal; }
    public String getEstadoAprobacion() { return estadoAprobacion; }
    public String getDetalle() { return detalle; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }

    public void setId(Long id) { this.id = id; }
    public void setMontoRepuestos(Double montoRepuestos) { this.montoRepuestos = montoRepuestos; }
    public void setManoObra(Double manoObra) { this.manoObra = manoObra; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
    public void setEstadoAprobacion(String estadoAprobacion) { this.estadoAprobacion = estadoAprobacion; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }
}