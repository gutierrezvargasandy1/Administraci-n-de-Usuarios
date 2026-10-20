package com.utng.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO con la informacion publica de un usuario.
 *
 * Los controladores nunca devuelven la entidad JPA: siempre devuelven este DTO,
 * lo que reduce el acoplamiento con la base de datos y permite controlar
 * exactamente que informacion se expone.
 */
@Schema(name = "UsuarioResponse", description = "Informacion publica de un usuario.")
public class UsuarioResponse {

    @Schema(description = "Identificador generado por la base de datos.", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario.", example = "Juan Perez")
    private String nombre;

    @Schema(description = "Correo electronico unico.", example = "juan.perez@correo.com")
    private String correo;

    @Schema(description = "Telefono de diez digitos.", example = "4771234567")
    private String telefono;

    @Schema(description = "Estado del usuario.", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creacion asignada por el servidor.", example = "2026-10-14T10:30:00")
    private LocalDateTime fechaCreacion;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombre, String correo, String telefono,
                           Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
