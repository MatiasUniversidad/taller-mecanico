package com.example.signin.dto;

public class DiagnosticoDTO {

    private String fallas;
    private String observaciones;
    private String recomendaciones;
    private Long ordenTrabajoId;

    public DiagnosticoDTO() {}

    public String getFallas() {
        return fallas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public Long getOrdenTrabajoId() {
        return ordenTrabajoId;
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

    public void setOrdenTrabajoId(Long ordenTrabajoId) {
        this.ordenTrabajoId = ordenTrabajoId;
    }
}