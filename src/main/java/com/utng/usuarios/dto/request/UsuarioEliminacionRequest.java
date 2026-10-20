package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO de la baja de usuarios (DELETE /api/v1/usuarios/eliminacion).
 *
 * Regla definida para el proyecto: la eliminacion es LOGICA de manera
 * predeterminada (se conserva el registro historico y se marca activo = false).
 * La eliminacion fisica solo se ejecuta cuando el cliente la solicita de forma
 * explicita con eliminacionFisica = true.
 */
@Schema(name = "UsuarioEliminacionRequest",
        description = "Llave compuesta del usuario a dar de baja y modalidad de eliminacion.")
public class UsuarioEliminacionRequest {

    @Schema(description = "Nombre exacto del usuario.", example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio y no puede contener solo espacios.")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
    private String nombre;

    @Schema(description = "Correo electronico exacto del usuario.", example = "juan.perez@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electronico es obligatorio.")
    @Email(message = "El correo electronico no tiene un formato valido.")
    @Size(max = 150, message = "El correo electronico no debe exceder 150 caracteres.")
    private String correo;

    @Schema(description = "true ejecuta la eliminacion fisica del registro. Si se omite, se aplica baja logica.",
            example = "false", defaultValue = "false")
    private Boolean eliminacionFisica;

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

    public Boolean getEliminacionFisica() {
        return eliminacionFisica;
    }

    public void setEliminacionFisica(Boolean eliminacionFisica) {
        this.eliminacionFisica = eliminacionFisica;
    }

    /** Devuelve la modalidad efectiva: logica cuando el cliente omite el campo. */
    public boolean esEliminacionFisica() {
        return Boolean.TRUE.equals(eliminacionFisica);
    }
}
