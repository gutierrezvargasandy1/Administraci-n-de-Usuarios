package com.utng.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Envoltura de las respuestas que devuelven colecciones paginadas.
 *
 * { "mensaje": "...", "datos": [ ... ], "paginacion": { ... } }
 *
 * @param <T> tipo del Response DTO transportado en la lista
 */
@Schema(name = "RespuestaPaginada", description = "Envoltura estandar de los listados paginados.")
public class RespuestaPaginada<T> {

    @Schema(description = "Mensaje descriptivo del resultado de la consulta.",
            example = "Consulta realizada correctamente.")
    private String mensaje;

    @Schema(description = "Registros de la pagina solicitada.")
    private List<T> datos;

    @Schema(description = "Metadatos de la pagina devuelta.")
    private MetadatosPaginacion paginacion;

    public RespuestaPaginada() {
    }

    public RespuestaPaginada(String mensaje, List<T> datos, MetadatosPaginacion paginacion) {
        this.mensaje = mensaje;
        this.datos = datos;
        this.paginacion = paginacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<T> getDatos() {
        return datos;
    }

    public void setDatos(List<T> datos) {
        this.datos = datos;
    }

    public MetadatosPaginacion getPaginacion() {
        return paginacion;
    }

    public void setPaginacion(MetadatosPaginacion paginacion) {
        this.paginacion = paginacion;
    }
}
