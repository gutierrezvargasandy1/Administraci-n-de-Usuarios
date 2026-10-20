package com.utng.usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza durante la actualizacion parcial cuando el cliente envia de forma
 * EXPLICITA el valor null en un campo obligatorio.
 *
 * Permite distinguir dos situaciones que en JSON se ven parecidas:
 * - el campo se omite  -> no se modifica (comportamiento valido);
 * - el campo llega con null -> intento de borrar un dato obligatorio (error).
 */
public class CampoNoAnulableException extends NegocioException {

    private static final long serialVersionUID = 1L;

    private final String campo;

    public CampoNoAnulableException(String campo) {
        super("El campo '%s' es obligatorio y no admite el valor null. Omitalo si no desea modificarlo."
                .formatted(campo));
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }

    @Override
    public String getCodigo() {
        return "CAMPO_NO_ANULABLE";
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.BAD_REQUEST;
    }
}
