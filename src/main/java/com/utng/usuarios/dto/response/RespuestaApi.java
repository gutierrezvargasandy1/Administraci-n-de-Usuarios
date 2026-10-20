package com.utng.usuarios.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Envoltura generica de las respuestas exitosas que devuelven un solo recurso.
 *
 * Mantiene una estructura de respuesta consistente en toda la API:
 * { "mensaje": "...", "datos": { ... } }
 *
 * @param <T> tipo del Response DTO transportado
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RespuestaApi", description = "Envoltura estandar de las respuestas exitosas.")
public class RespuestaApi<T> {

    @Schema(description = "Mensaje descriptivo del resultado de la operacion.",
            example = "Usuario registrado correctamente.")
    private String mensaje;

    @Schema(description = "Carga util de la respuesta.")
    private T datos;

    public RespuestaApi() {
    }

    public RespuestaApi(String mensaje, T datos) {
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static <T> RespuestaApi<T> de(String mensaje, T datos) {
        return new RespuestaApi<>(mensaje, datos);
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }
}
