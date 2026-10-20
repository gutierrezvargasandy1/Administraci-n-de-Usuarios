package com.utng.usuarios.mapper;

import java.util.List;

/**
 * INTERFAZ generica de conversion entre entidades y Response DTOs.
 *
 * Declara una sola operacion abstracta y aporta una implementacion por defecto
 * para las colecciones, de modo que cualquier mapeador del proyecto obtiene la
 * conversion de listas sin reescribirla. El codigo de servicio depende de esta
 * interfaz y no de una implementacion concreta.
 *
 * @param <E> tipo de la entidad de origen
 * @param <R> tipo del Response DTO de destino
 */
public interface Mapeador<E, R> {

    /** Convierte una entidad en su Response DTO. */
    R aRespuesta(E entidad);

    /** Convierte una lista completa reutilizando la conversion individual. */
    default List<R> aRespuestas(List<E> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        return entidades.stream().map(this::aRespuesta).toList();
    }
}
