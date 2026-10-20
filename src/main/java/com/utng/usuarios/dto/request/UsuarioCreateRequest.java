package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO para el alta de usuarios (POST /api/v1/usuarios).
 *
 * No expone el identificador ni la fecha de creacion: ambos son campos
 * controlados por el servidor, por lo que el cliente no puede establecerlos.
 */
@Schema(name = "UsuarioCreateRequest", description = "Datos necesarios para registrar un usuario nuevo.")
public class UsuarioCreateRequest {

    @Schema(description = "Nombre completo del usuario.", example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio y no puede contener solo espacios.")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
    private String nombre;

    @Schema(description = "Correo electronico unico del usuario.", example = "juan.perez@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electronico es obligatorio.")
    @Email(message = "El correo electronico no tiene un formato valido.")
    @Size(max = 150, message = "El correo electronico no debe exceder 150 caracteres.")
    private String correo;

    @Schema(description = "Telefono de diez digitos.", example = "4771234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El telefono es obligatorio.")
    @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener exactamente 10 digitos.")
    private String telefono;

    @Schema(description = "Estado inicial del usuario. Si se omite, el servidor registra el usuario como activo.", example = "true")
    private Boolean activo;

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
}
