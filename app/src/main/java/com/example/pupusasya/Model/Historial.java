package com.example.pupusasya.Model;

public class Historial {
    int idHistorial;
    String Detalle, PrecioTotal, Delivery, Nombre, Estado ;

    public Historial() {
    }


    public Historial(int idHistorial, String detalle, String precioTotal, String delivery, String nombre, String estado) {
        this.idHistorial = idHistorial;
        Detalle = detalle;
        PrecioTotal = precioTotal;
        Delivery = delivery;
        Nombre = nombre;
        Estado = estado;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public String getDetalle() {
        return Detalle;
    }

    public void setDetalle(String detalle) {
        Detalle = detalle;
    }

    public String getPrecioTotal() {
        return PrecioTotal;
    }

    public void setPrecioTotal(String precioTotal) {
        PrecioTotal = precioTotal;
    }

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
