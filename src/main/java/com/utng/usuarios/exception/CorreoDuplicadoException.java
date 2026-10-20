package com.utng.usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza al intentar registrar o actualizar un usuario con un correo
 * electronico que ya se encuentra registrado. Se traduce en HTTP 409.
 */
public class CorreoDuplicadoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public CorreoDuplicadoException() {
        super("El correo ya se encuentra registrado. Intente con otro correo.");
    }

    public CorreoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getCodigo() {
        return "CORREO_DUPLICADO";
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.CONFLICT;
    }
}
