package com.example.signin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostico")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String fallas;

    @Column(length = 1000)
    private String observaciones;

    @Column(length = 1000)
    private String recomendaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @OneToOne
    @JoinColumn(name = "orden_trabajo_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"vehiculo", "mecanico"})
    private OrdenTrabajo ordenTrabajo;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }

    public Diagnostico() {}

    public Long getId() {
        return id;
    }

    public String getFallas() {
        return fallas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public OrdenTrabajo getOrdenTrabajo() {
        return ordenTrabajo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFallas(String fallas) {
        this.fallas = fallas;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
        this.ordenTrabajo = ordenTrabajo;
    }
}