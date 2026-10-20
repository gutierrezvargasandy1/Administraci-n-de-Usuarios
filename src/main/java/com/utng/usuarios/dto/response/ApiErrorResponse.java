package com.utng.usuarios.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato unico de todas las respuestas de error de la API.
 *
 * Lo construye exclusivamente el GlobalExceptionHandler, de modo que ningun
 * controlador arma respuestas de error por su cuenta. Nunca transporta
 * sentencias SQL, nombres internos de tablas ni trazas de ejecucion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiErrorResponse", description = "Estructura estandar de los errores de la API.")
public class ApiErrorResponse {

    @Schema(description = "Codigo de negocio estable.", example = "DATOS_INVALIDOS")
    private String codigo;

    @Schema(description = "Mensaje orientado al usuario final.",
            example = "Verifique la informacion capturada e intente nuevamente.")
    private String mensaje;

    @Schema(description = "Detalle por campo. Solo se incluye en los errores de validacion.")
    private Map<String, String> errores;

    @Schema(description = "Ruta que origino el error.", example = "/api/v1/usuarios")
    private String ruta;

    @Schema(description = "Momento en que se genero el error.", example = "2026-10-14T10:30:00")
    private LocalDateTime marcaTiempo;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String codigo, String mensaje, Map<String, String> errores, String ruta) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.errores = errores;
        this.ruta = ruta;
        this.marcaTiempo = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    public void setErrores(Map<String, String> errores) {
        this.errores = errores;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public LocalDateTime getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(LocalDateTime marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }
}
