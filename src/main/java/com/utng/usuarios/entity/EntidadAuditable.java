package com.utng.usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * CLASE ABSTRACTA base de todas las entidades persistentes del dominio.
 *
 * Concentra los campos controlados por el servidor (identificador y marcas de
 * tiempo) para que ninguna subclase ni DTO permita que el cliente los establezca.
 *
 * Declara el metodo abstracto {@link #descripcionAuditoria()} que cada entidad
 * concreta debe redefinir: ese metodo es el punto de POLIMORFISMO de la jerarquia,
 * ya que el registro de bitacora invoca la misma firma sin conocer el tipo real.
 */
@MappedSuperclass
public abstract class EntidadAuditable implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador primario generado por la base de datos (IDENTITY / BIGSERIAL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Fecha de creacion controlada por el servidor; nunca se recibe del cliente. */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha de la ultima modificacion, tambien controlada por el servidor. */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Descripcion legible de la entidad para la bitacora de la aplicacion.
     * Cada subclase concreta define su propia version (polimorfismo).
     */
    public abstract String descripcionAuditoria();

    @PrePersist
    protected void alCrear() {
        this.fechaCreacion = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    protected void alActualizar() {
        this.fechaActualizacion = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
