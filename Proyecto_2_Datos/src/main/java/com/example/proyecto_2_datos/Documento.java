package com.example.proyecto_2_datos;

public class Documento {
    private String nombre;
    private String ruta; // Nueva propiedad para almacenar la ruta del archivo asociado

    public Documento(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public String toString() {
        return nombre;
    }
}



