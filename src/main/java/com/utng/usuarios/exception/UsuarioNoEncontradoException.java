package com.utng.usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando no existe un usuario que coincida con la llave compuesta
 * formada por el nombre y el correo electronico. Se traduce en HTTP 404.
 */
public class UsuarioNoEncontradoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public UsuarioNoEncontradoException() {
        super("No existe un usuario con el nombre y correo electronico proporcionados.");
    }

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getCodigo() {
        return "USUARIO_NO_ENCONTRADO";
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.NOT_FOUND;
    }
}
