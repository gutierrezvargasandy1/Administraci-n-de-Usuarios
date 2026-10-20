package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO de la consulta por LLAVE COMPUESTA nombre + correo
 * (POST /api/v1/usuarios/consulta).
 *
 * Ambos campos son obligatorios: la consulta solo tiene sentido cuando se
 * proporcionan los dos criterios que forman la llave.
 */
@Schema(name = "UsuarioConsultaRequest",
        description = "Llave compuesta por nombre y correo electronico utilizada para localizar a un usuario.")
public class UsuarioConsultaRequest {

    @Schema(description = "Nombre exacto del usuario.", example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio y no puede contener solo espacios.")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
    private String nombre;

    @Schema(description = "Correo electronico exacto del usuario.", example = "juan.perez@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electronico es obligatorio.")
    @Email(message = "El correo electronico no tiene un formato valido.")
    @Size(max = 150, message = "El correo electronico no debe exceder 150 caracteres.")
    private String correo;

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
}
