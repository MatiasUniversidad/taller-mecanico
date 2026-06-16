package com.example.signin.dto;

public class RepuestoDTO {

    private String codigo;
    private String nombre;
    private Integer stock;
    private Double precioUnitario;

    public RepuestoDTO() {}

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}