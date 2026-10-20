package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Bloque de ordenamiento que viaja dentro del Request DTO de busqueda.
 *
 * Demuestra el uso correcto de tres anotaciones distintas sobre tipos distintos:
 * - @NotEmpty sobre la COLECCION: si el bloque se envia, debe traer al menos un campo.
 * - @NotBlank sobre cada ELEMENTO de la coleccion: ningun nombre de campo puede
 *   ser una cadena vacia o formada solo por espacios.
 * - @NotNull sobre el booleano: es obligatorio y admite el valor false, por lo que
 *   @NotEmpty o @NotBlank serian incorrectas en este campo.
 */
@Schema(name = "OrdenRequest", description = "Campos por los que se ordena el resultado y sentido del ordenamiento.")
public class OrdenRequest {

    @Schema(description = "Campos de ordenamiento permitidos: id, nombre, correo, telefono, activo, fechaCreacion.",
            example = "[\"nombre\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Debe indicar al menos un campo de ordenamiento.")
    @Size(max = 5, message = "No se permiten mas de 5 campos de ordenamiento.")
    private List<@NotBlank(message = "El campo de ordenamiento no puede estar vacio.") String> campos;

    @Schema(description = "true para orden ascendente, false para descendente.", example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Debe indicar el sentido del ordenamiento (true ascendente, false descendente).")
    private Boolean ascendente;

    public List<String> getCampos() {
        return campos;
    }

    public void setCampos(List<String> campos) {
        this.campos = campos;
    }

    public Boolean getAscendente() {
        return ascendente;
    }

    public void setAscendente(Boolean ascendente) {
        this.ascendente = ascendente;
    }
}
