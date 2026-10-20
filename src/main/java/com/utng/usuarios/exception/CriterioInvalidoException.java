package com.utng.usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando la busqueda dinamica recibe un criterio que la aplicacion no
 * puede resolver, por ejemplo un campo de ordenamiento inexistente. Evita que
 * un nombre de propiedad enviado por el cliente llegue a la capa de persistencia.
 */
public class CriterioInvalidoException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public CriterioInvalidoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getCodigo() {
        return "CRITERIO_INVALIDO";
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.BAD_REQUEST;
    }
}
