package com.example.signin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_pago")
public class DocumentoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    private String numero;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Double monto;

    @Column(name = "tipo_documento", nullable = false, length = 30)
    private String tipoDocumento;

    @OneToOne
    @JoinColumn(name = "presupuesto_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"ordenTrabajo"})
    private Presupuesto presupuesto;

    @OneToOne
    @JoinColumn(name = "orden_trabajo_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"vehiculo", "mecanico"})
    private OrdenTrabajo ordenTrabajo;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();

        if (this.numero == null || this.numero.isBlank()) {
            this.numero = "DOC-" + System.currentTimeMillis();
        }
    }

    public DocumentoPago() {}

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public LocalDateTime getFecha() { return fecha; }
    public Double getMonto() { return monto; }
    public String getTipoDocumento() { return tipoDocumento; }
    public Presupuesto getPresupuesto() { return presupuesto; }
    public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }

    public void setId(Long id) { this.id = id; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public void setMonto(Double monto) { this.monto = monto; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public void setPresupuesto(Presupuesto presupuesto) { this.presupuesto = presupuesto; }
    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }
}