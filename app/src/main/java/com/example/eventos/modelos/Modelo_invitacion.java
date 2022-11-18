package com.example.eventos.modelos;

import java.io.Serializable;

public class Modelo_invitacion implements Serializable {
    String id, id_evento,id_usuario_referido,nombre_evento, nombre_referido, nombre, telefono, correo, estado, apline_referido, usuario_validus_referido, telefono_referido, nuevo_usuario_validus, nuevo_codigo_validus, fecha, costo, ciudad, direccion;

    public Modelo_invitacion() {
    }

    public String getTelefono_referido() {
        return telefono_referido;
    }

    public void setTelefono_referido(String telefono_referido) {
        this.telefono_referido = telefono_referido;
    }

    public String getApline_referido() {
        return apline_referido;
    }

    public void setApline_referido(String apline_referido) {
        this.apline_referido = apline_referido;
    }

    public String getUsuario_validus_referido() {
        return usuario_validus_referido;
    }

    public void setUsuario_validus_referido(String usuario_validus_referido) {
        this.usuario_validus_referido = usuario_validus_referido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId_evento(String id_evento) {
        this.id_evento = id_evento;
    }

    public String getId_usuario_referido() {
        return id_usuario_referido;
    }

    public void setId_usuario_referido(String id_usuario_referido) {
        this.id_usuario_referido = id_usuario_referido;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getNombre_referido() {
        return nombre_referido;
    }

    public void setNombre_referido(String nombre_referido) {
        this.nombre_referido = nombre_referido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNuevo_usuario_validus() {
        return nuevo_usuario_validus;
    }

    public void setNuevo_usuario_validus(String nuevo_usuario_validus) {
        this.nuevo_usuario_validus = nuevo_usuario_validus;
    }

    public String getNuevo_codigo_validus() {
        return nuevo_codigo_validus;
    }

    public void setNuevo_codigo_validus(String nuevo_codigo_validus) {
        this.nuevo_codigo_validus = nuevo_codigo_validus;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
