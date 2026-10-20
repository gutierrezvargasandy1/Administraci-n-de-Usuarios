package com.utng.usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * CLASE ABSTRACTA raiz de todas las excepciones de negocio de la aplicacion.
 *
 * Cada subclase declara su propio codigo de error y su propio codigo HTTP.
 * Gracias a ello el {@link GlobalExceptionHandler} atiende a TODAS las
 * excepciones de negocio con un unico metodo @ExceptionHandler que invoca
 * getCodigo() y getEstado() sobre la referencia del tipo abstracto:
 * ese es el POLIMORFISMO aplicado al manejo centralizado de errores.
 */
public abstract class NegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected NegocioException(String mensaje) {
        super(mensaje);
    }

    /** Codigo de negocio estable que el cliente puede interpretar. */
    public abstract String getCodigo();

    /** Codigo HTTP con el que se debe responder esta situacion. */
    public abstract HttpStatus getEstado();
}
