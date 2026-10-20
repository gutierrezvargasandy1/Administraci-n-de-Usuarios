package com.utng.usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * CLASE ABSTRACTA intermedia de la jerarquia de entidades.
 *
 * Agrupa los datos comunes a cualquier persona registrada en el sistema
 * (nombre, correo, telefono y estado) y HEREDA de {@link EntidadAuditable}.
 *
 * Implementa {@link #descripcionAuditoria()} apoyandose en el metodo abstracto
 * {@link #tipoPersona()}: la superclase resuelve el formato y la subclase aporta
 * el dato concreto, que es el mecanismo de polimorfismo de la jerarquia.
 */
@MappedSuperclass
public abstract class Persona extends EntidadAuditable {

    private static final long serialVersionUID = 1L;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    /** Etiqueta del tipo concreto de persona. Cada subclase la redefine. */
    public abstract String tipoPersona();

    @Override
    public String descripcionAuditoria() {
        return "%s [id=%s, nombre=%s, correo=%s]".formatted(tipoPersona(), getId(), nombre, correo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
