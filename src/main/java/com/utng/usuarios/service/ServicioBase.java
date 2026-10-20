package com.utng.usuarios.service;

import com.utng.usuarios.entity.EntidadAuditable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASE ABSTRACTA generica de la capa de servicio.
 *
 * Implementa el comportamiento comun a cualquier servicio de entidad
 * (registro en bitacora y conteo) y deja abstractos los dos puntos que cambian
 * en cada caso: el repositorio concreto y el nombre del recurso.
 *
 * La llamada entidad.descripcionAuditoria() dentro de persistir() es una
 * invocacion POLIMORFICA: esta clase no conoce el tipo real de la entidad y
 * aun asi obtiene la descripcion correcta de cada una.
 *
 * @param <E>  tipo de entidad administrada, descendiente de EntidadAuditable
 * @param <ID> tipo del identificador de la entidad
 */
public abstract class ServicioBase<E extends EntidadAuditable, ID> {

    protected final Logger bitacora = LoggerFactory.getLogger(getClass());

    /** Repositorio concreto que utiliza el servicio descendiente. */
    protected abstract JpaRepository<E, ID> repositorio();

    /** Nombre del recurso administrado, usado en los mensajes de bitacora. */
    protected abstract String nombreRecurso();

    /** Total de registros almacenados del recurso. */
    @Transactional(readOnly = true)
    public long contar() {
        return repositorio().count();
    }

    /** Guarda la entidad y deja constancia en la bitacora de la aplicacion. */
    protected E persistir(E entidad) {
        E almacenada = repositorio().save(entidad);
        bitacora.info("{} almacenado correctamente -> {}", nombreRecurso(), almacenada.descripcionAuditoria());
        return almacenada;
    }
}
