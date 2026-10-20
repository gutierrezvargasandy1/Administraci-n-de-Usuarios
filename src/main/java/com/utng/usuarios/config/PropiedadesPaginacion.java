package com.utng.usuarios.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de paginacion externalizadas en application.yml (prefijo app.paginacion).
 *
 * Se utilizan para evitar consultas excesivamente costosas cuando el cliente no
 * proporciona filtros ni tamanio de pagina.
 *
 * @param tamanioPorDefecto numero de registros devueltos cuando el cliente no indica tamanio
 * @param tamanioMaximo     tope duro de registros por pagina
 */
@ConfigurationProperties(prefix = "app.paginacion")
public record PropiedadesPaginacion(Integer tamanioPorDefecto, Integer tamanioMaximo) {

    public int tamanioPorDefectoSeguro() {
        return tamanioPorDefecto == null || tamanioPorDefecto < 1 ? 10 : tamanioPorDefecto;
    }

    public int tamanioMaximoSeguro() {
        return tamanioMaximo == null || tamanioMaximo < 1 ? 50 : tamanioMaximo;
    }

    /** Limita el tamanio solicitado por el cliente al tope configurado. */
    public int ajustar(Integer tamanioSolicitado) {
        if (tamanioSolicitado == null || tamanioSolicitado < 1) {
            return tamanioPorDefectoSeguro();
        }
        return Math.min(tamanioSolicitado, tamanioMaximoSeguro());
    }
}
