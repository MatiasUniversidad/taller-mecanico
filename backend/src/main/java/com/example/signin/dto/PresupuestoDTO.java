package com.example.signin.dto;

public class PresupuestoDTO {

    private Long ordenTrabajoId;
    private Double manoObra;
    private String detalle;

    public PresupuestoDTO() {}

    public Long getOrdenTrabajoId() { return ordenTrabajoId; }
    public Double getManoObra() { return manoObra; }
    public String getDetalle() { return detalle; }

    public void setOrdenTrabajoId(Long ordenTrabajoId) { this.ordenTrabajoId = ordenTrabajoId; }
    public void setManoObra(Double manoObra) { this.manoObra = manoObra; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}