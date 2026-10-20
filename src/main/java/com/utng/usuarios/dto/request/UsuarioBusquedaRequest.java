package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

/**
 * Request DTO de la BUSQUEDA DINAMICA (POST /api/v1/usuarios/busqueda).
 *
 * Todos los criterios son opcionales y se combinan entre si con el operador
 * logico AND. Los campos que no se proporcionan se excluyen de la consulta, de
 * modo que un unico endpoint y una unica Specification resuelven cualquier
 * combinacion de filtros, sin crear un servicio por cada campo.
 */
@Schema(name = "UsuarioBusquedaRequest",
        description = "Criterios opcionales de busqueda. Los campos omitidos no participan en la consulta.")
public class UsuarioBusquedaRequest {

    @Schema(description = "Coincidencia parcial e insensible a mayusculas sobre el nombre.", example = "Juan")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
    private String nombre;

    @Schema(description = "Coincidencia parcial e insensible a mayusculas sobre el correo.", example = "correo.com")
    @Size(max = 150, message = "El correo no debe exceder 150 caracteres.")
    private String correo;

    @Schema(description = "Coincidencia parcial sobre el telefono.", example = "477")
    @Size(max = 20, message = "El telefono no debe exceder 20 caracteres.")
    private String telefono;

    @Schema(description = "Coincidencia exacta sobre el estado del usuario.", example = "true")
    private Boolean activo;

    @Schema(description = "Rango opcional de fecha de creacion.")
    @Valid
    private RangoFechasRequest rangoFechaCreacion;

    @Schema(description = "Paginacion opcional. Si se omite se aplican los valores configurados en el servidor.")
    @Valid
    private PaginacionRequest paginacion;

    @Schema(description = "Ordenamiento opcional. Si se omite se ordena por id ascendente.")
    @Valid
    private OrdenRequest orden;

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

    public RangoFechasRequest getRangoFechaCreacion() {
        return rangoFechaCreacion;
    }

    public void setRangoFechaCreacion(RangoFechasRequest rangoFechaCreacion) {
        this.rangoFechaCreacion = rangoFechaCreacion;
    }

    public PaginacionRequest getPaginacion() {
        return paginacion;
    }

    public void setPaginacion(PaginacionRequest paginacion) {
        this.paginacion = paginacion;
    }

    public OrdenRequest getOrden() {
        return orden;
    }

    public void setOrden(OrdenRequest orden) {
        this.orden = orden;
    }
}
