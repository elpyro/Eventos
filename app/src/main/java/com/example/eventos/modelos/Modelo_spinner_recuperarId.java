package com.example.eventos.modelos;

import androidx.annotation.NonNull;

public class Modelo_spinner_recuperarId {

 
        String id, nombre, vista;

    public Modelo_spinner_recuperarId(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }
    @NonNull
    @Override
    public String toString(){
        vista=nombre;
        return vista;
    }
}
