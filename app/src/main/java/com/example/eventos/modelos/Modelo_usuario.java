package com.example.eventos.modelos;

public class Modelo_usuario {
    String id,  perfil,nombre, telefono, correoGmail, direccion, apline, validusUsuario;

    public Modelo_usuario() {
    }

    public Modelo_usuario(String id, String perfil, String nombre, String telefono, String correoGmail, String direccion, String apline, String validusUsuario) {
        this.id = id;
        this.perfil = perfil;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correoGmail = correoGmail;
        this.direccion = direccion;
        this.apline = apline;
        this.validusUsuario = validusUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
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

    public String getCorreoGmail() {
        return correoGmail;
    }

    public void setCorreoGmail(String correoGmail) {
        this.correoGmail = correoGmail;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getApline() {
        return apline;
    }

    public void setApline(String apline) {
        this.apline = apline;
    }

    public String getValidusUsuario() {
        return validusUsuario;
    }

    public void setValidusUsuario(String validusUsuario) {
        this.validusUsuario = validusUsuario;
    }
}
