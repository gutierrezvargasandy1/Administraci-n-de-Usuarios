package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;

/**
 * Request DTO de la actualizacion parcial (PATCH /api/v1/usuarios/actualizacion).
 *
 * LOCALIZACION DEL USUARIO
 * Los campos nombre y correo son obligatorios y funcionan como LLAVE COMPUESTA:
 * identifican al usuario ANTES de aplicar cualquier cambio. Nunca se modifican
 * por si mismos.
 *
 * CAMBIO DE LA PROPIA LLAVE
 * Para renombrar al usuario o cambiarle el correo se utilizan los campos
 * nuevoNombre y nuevoCorreo. De esta forma la llave de busqueda y el valor
 * nuevo viajan por separado y nunca se confunden.
 *
 * OMITIDO FRENTE A NULL EXPLICITO
 * Los campos modificables se declaran como Optional. Jackson permite distinguir
 * las tres situaciones:
 * - el campo NO aparece en el JSON  -> el atributo queda en null  -> no se modifica;
 * - el campo aparece con valor null -> el atributo queda en Optional.empty() -> intento
 *   de borrar un dato obligatorio, que se rechaza con CampoNoAnulableException;
 * - el campo aparece con valor       -> Optional.of(valor) -> se aplica el cambio.
 * Asi se evita sobrescribir informacion con valores nulos de manera accidental.
 */
@Schema(name = "UsuarioUpdateRequest",
        description = "Llave compuesta del usuario y campos que se desean modificar. Los campos omitidos no se modifican.")
public class UsuarioUpdateRequest {

    @Schema(description = "Nombre actual del usuario. Forma parte de la llave de busqueda.",
            example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio y no puede contener solo espacios.")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
    private String nombre;

    @Schema(description = "Correo actual del usuario. Forma parte de la llave de busqueda.",
            example = "juan.perez@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electronico es obligatorio.")
    @Email(message = "El correo electronico no tiene un formato valido.")
    @Size(max = 150, message = "El correo electronico no debe exceder 150 caracteres.")
    private String correo;

    @Schema(description = "Nuevo nombre. Omitalo si no desea renombrar al usuario.", example = "Juan A. Perez")
    private Optional<
            @NotBlank(message = "El nuevo nombre no puede quedar vacio ni contener solo espacios.")
            @Size(max = 120, message = "El nuevo nombre no debe exceder 120 caracteres.")
            String> nuevoNombre;

    @Schema(description = "Nuevo correo electronico. Se valida que no pertenezca a otro usuario.",
            example = "juan.a.perez@correo.com")
    private Optional<
            @NotBlank(message = "El nuevo correo no puede quedar vacio ni contener solo espacios.")
            @Email(message = "El nuevo correo electronico no tiene un formato valido.")
            @Size(max = 150, message = "El nuevo correo no debe exceder 150 caracteres.")
            String> nuevoCorreo;

    @Schema(description = "Nuevo telefono de diez digitos.", example = "4779876543")
    private Optional<
            @NotBlank(message = "El telefono no puede quedar vacio ni contener solo espacios.")
            @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener exactamente 10 digitos.")
            String> telefono;

    @Schema(description = "Nuevo estado del usuario.", example = "true")
    private Optional<Boolean> activo;

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

    public Optional<String> getNuevoNombre() {
        return nuevoNombre;
    }

    public void setNuevoNombre(Optional<String> nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
    }

    public Optional<String> getNuevoCorreo() {
        return nuevoCorreo;
    }

    public void setNuevoCorreo(Optional<String> nuevoCorreo) {
        this.nuevoCorreo = nuevoCorreo;
    }

    public Optional<String> getTelefono() {
        return telefono;
    }

    public void setTelefono(Optional<String> telefono) {
        this.telefono = telefono;
    }

    public Optional<Boolean> getActivo() {
        return activo;
    }

    public void setActivo(Optional<Boolean> activo) {
        this.activo = activo;
    }

    /** true solo cuando el cliente incluyo el campo en el cuerpo de la peticion. */
    public boolean seEnvioNuevoNombre() {
        return nuevoNombre != null;
    }

    public boolean seEnvioNuevoCorreo() {
        return nuevoCorreo != null;
    }

    public boolean seEnvioTelefono() {
        return telefono != null;
    }

    public boolean seEnvioActivo() {
        return activo != null;
    }

    /** true cuando no se recibio ningun campo modificable. */
    public boolean sinCambiosSolicitados() {
        return !seEnvioNuevoNombre() && !seEnvioNuevoCorreo() && !seEnvioTelefono() && !seEnvioActivo();
    }
}
