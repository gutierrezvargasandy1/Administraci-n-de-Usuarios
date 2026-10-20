package com.utng.usuarios.service;

import com.utng.usuarios.dto.request.UsuarioBusquedaRequest;
import com.utng.usuarios.dto.request.UsuarioConsultaRequest;
import com.utng.usuarios.dto.request.UsuarioCreateRequest;
import com.utng.usuarios.dto.request.UsuarioEliminacionRequest;
import com.utng.usuarios.dto.request.UsuarioUpdateRequest;
import com.utng.usuarios.dto.response.RespuestaPaginada;
import com.utng.usuarios.dto.response.UsuarioResponse;

/**
 * INTERFAZ del servicio de administracion de usuarios.
 *
 * El controlador depende unicamente de este contrato, nunca de la clase que lo
 * implementa: ese desacoplamiento permite sustituir o extender la logica de
 * negocio sin tocar la capa web.
 *
 * El contrato esta expresado solo con DTOs. Ninguna entidad JPA cruza esta
 * frontera hacia la capa de presentacion.
 */
public interface UsuarioService {

    /** Alta de un usuario nuevo. */
    UsuarioResponse crear(UsuarioCreateRequest solicitud);

    /** Listado paginado de los usuarios registrados, con la configuracion por defecto. */
    RespuestaPaginada<UsuarioResponse> listar();

    /** Consulta de un usuario por la llave compuesta nombre + correo. */
    UsuarioResponse consultarPorLlaveCompuesta(UsuarioConsultaRequest solicitud);

    /** Busqueda dinamica que combina cualquier subconjunto de criterios. */
    RespuestaPaginada<UsuarioResponse> buscar(UsuarioBusquedaRequest criterios);

    /** Actualizacion parcial del usuario localizado por su llave compuesta. */
    UsuarioResponse actualizar(UsuarioUpdateRequest solicitud);

    /** Baja logica o fisica del usuario localizado por su llave compuesta. */
    ResultadoEliminacion eliminar(UsuarioEliminacionRequest solicitud);

    /**
     * Resultado de una baja: informacion del usuario afectado y modalidad aplicada.
     *
     * @param usuario informacion del usuario en el momento de la baja
     * @param fisica  true si el registro se elimino fisicamente de la base de datos
     */
    record ResultadoEliminacion(UsuarioResponse usuario, boolean fisica) {
    }
}
