package com.utng.usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad concreta persistida en la tabla "usuarios".
 *
 * HERENCIA: Usuario -> Persona -> EntidadAuditable.
 * La restriccion UNIQUE del correo se declara tanto aqui como en el script de
 * la base de datos, porque las validaciones de la aplicacion no sustituyen a las
 * restricciones de integridad del manejador.
 */
@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo"),
        indexes = {
                @Index(name = "ix_usuarios_nombre_correo", columnList = "nombre, correo"),
                @Index(name = "ix_usuarios_activo", columnList = "activo")
        }
)
public class Usuario extends Persona {

    private static final long serialVersionUID = 1L;

    /** Momento en que se aplico la baja logica. Nulo mientras el usuario esta vigente. */
    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    public Usuario() {
        // Constructor requerido por JPA.
    }

    public Usuario(String nombre, String correo, String telefono, Boolean activo) {
        setNombre(nombre);
        setCorreo(correo);
        setTelefono(telefono);
        setActivo(activo == null ? Boolean.TRUE : activo);
    }

    /** Redefinicion polimorfica usada por la bitacora y por la capa de servicio. */
    @Override
    public String tipoPersona() {
        return "Usuario";
    }

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Usuario otro)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), otro.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return descripcionAuditoria();
    }
}
