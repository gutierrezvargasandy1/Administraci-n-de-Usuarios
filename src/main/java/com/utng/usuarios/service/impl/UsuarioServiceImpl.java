package com.utng.usuarios.service.impl;

import com.utng.usuarios.config.PropiedadesPaginacion;
import com.utng.usuarios.dto.request.OrdenRequest;
import com.utng.usuarios.dto.request.PaginacionRequest;
import com.utng.usuarios.dto.request.UsuarioBusquedaRequest;
import com.utng.usuarios.dto.request.UsuarioConsultaRequest;
import com.utng.usuarios.dto.request.UsuarioCreateRequest;
import com.utng.usuarios.dto.request.UsuarioEliminacionRequest;
import com.utng.usuarios.dto.request.UsuarioUpdateRequest;
import com.utng.usuarios.dto.response.MetadatosPaginacion;
import com.utng.usuarios.dto.response.RespuestaPaginada;
import com.utng.usuarios.dto.response.UsuarioResponse;
import com.utng.usuarios.entity.Usuario;
import com.utng.usuarios.exception.CampoNoAnulableException;
import com.utng.usuarios.exception.CorreoDuplicadoException;
import com.utng.usuarios.exception.CriterioInvalidoException;
import com.utng.usuarios.exception.UsuarioNoEncontradoException;
import com.utng.usuarios.mapper.UsuarioMapper;
import com.utng.usuarios.repository.UsuarioRepository;
import com.utng.usuarios.service.ServicioBase;
import com.utng.usuarios.service.UsuarioService;
import com.utng.usuarios.specification.UsuarioSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementacion de las reglas de negocio de la administracion de usuarios.
 *
 * HERENCIA  : extiende la clase abstracta {@link ServicioBase}.
 * INTERFACES: implementa el contrato {@link UsuarioService}.
 *
 * Toda la logica de negocio vive aqui: verificacion de existencia, unicidad del
 * correo, decision entre baja logica y fisica, armado de la paginacion y
 * construccion de los criterios dinamicos. El controlador no decide nada.
 */
@Service
public class UsuarioServiceImpl extends ServicioBase<Usuario, Long> implements UsuarioService {

    /** Campos por los que se permite ordenar. Evita que un valor del cliente llegue a la consulta. */
    private static final Set<String> CAMPOS_ORDENABLES =
            Set.of("id", "nombre", "correo", "telefono", "activo", "fechaCreacion");

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PropiedadesPaginacion propiedadesPaginacion;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              UsuarioMapper usuarioMapper,
                              PropiedadesPaginacion propiedadesPaginacion) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.propiedadesPaginacion = propiedadesPaginacion;
    }

    @Override
    protected JpaRepository<Usuario, Long> repositorio() {
        return usuarioRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Usuario";
    }

    // ------------------------------------------------------------------
    // 2.1 Alta
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest solicitud) {
        String correo = usuarioMapper.normalizarCorreo(solicitud.getCorreo());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new CorreoDuplicadoException();
        }

        Usuario usuario = usuarioMapper.aEntidad(solicitud);
        try {
            return usuarioMapper.aRespuesta(persistir(usuario));
        } catch (DataIntegrityViolationException excepcion) {
            // Cubre la condicion de carrera entre la verificacion previa y el INSERT:
            // la restriccion UNIQUE de la base de datos es la ultima linea de defensa.
            bitacora.warn("La restriccion de unicidad rechazo el alta del correo solicitado.");
            throw new CorreoDuplicadoException();
        }
    }

    // ------------------------------------------------------------------
    // 2.2 Listado
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public RespuestaPaginada<UsuarioResponse> listar() {
        Pageable pagina = PageRequest.of(0, propiedadesPaginacion.tamanioPorDefectoSeguro(),
                Sort.by(Sort.Direction.ASC, "id"));
        Page<Usuario> resultado = usuarioRepository.findAll(pagina);
        return armarRespuesta("Consulta realizada correctamente.", resultado);
    }

    // ------------------------------------------------------------------
    // 2.3 Consulta por llave compuesta
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse consultarPorLlaveCompuesta(UsuarioConsultaRequest solicitud) {
        Usuario usuario = localizar(solicitud.getNombre(), solicitud.getCorreo());
        return usuarioMapper.aRespuesta(usuario);
    }

    // ------------------------------------------------------------------
    // 7. Busqueda dinamica
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public RespuestaPaginada<UsuarioResponse> buscar(UsuarioBusquedaRequest criterios) {
        UsuarioSpecification especificacion = UsuarioSpecification.conCriterios(criterios);

        if (especificacion.sinCriterios()) {
            bitacora.debug("Busqueda sin criterios: se devuelve la pagina solicitada del listado completo.");
        }

        Pageable pagina = armarPaginacion(criterios);
        Page<Usuario> resultado = usuarioRepository.findAll(especificacion, pagina);
        return armarRespuesta("Busqueda realizada correctamente.", resultado);
    }

    // ------------------------------------------------------------------
    // 2.4 Actualizacion parcial
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public UsuarioResponse actualizar(UsuarioUpdateRequest solicitud) {
        if (solicitud.sinCambiosSolicitados()) {
            throw new CriterioInvalidoException(
                    "Debe proporcionar al menos un campo modificable: nuevoNombre, nuevoCorreo, telefono o activo.");
        }

        Usuario usuario = localizar(solicitud.getNombre(), solicitud.getCorreo());

        if (solicitud.seEnvioNuevoNombre()) {
            usuario.setNombre(valorObligatorio(solicitud.getNuevoNombre(), "nuevoNombre").trim());
        }

        if (solicitud.seEnvioNuevoCorreo()) {
            String correoNuevo = usuarioMapper.normalizarCorreo(
                    valorObligatorio(solicitud.getNuevoCorreo(), "nuevoCorreo"));
            if (!correoNuevo.equals(usuario.getCorreo())
                    && usuarioRepository.existsByCorreoAndIdNot(correoNuevo, usuario.getId())) {
                throw new CorreoDuplicadoException();
            }
            usuario.setCorreo(correoNuevo);
        }

        if (solicitud.seEnvioTelefono()) {
            usuario.setTelefono(valorObligatorio(solicitud.getTelefono(), "telefono").trim());
        }

        if (solicitud.seEnvioActivo()) {
            aplicarEstado(usuario, valorObligatorio(solicitud.getActivo(), "activo"));
        }

        // La llave compuesta debe seguir siendo unica despues de aplicar los cambios.
        if (usuarioRepository.existsByNombreAndCorreoAndIdNot(
                usuario.getNombre(), usuario.getCorreo(), usuario.getId())) {
            throw new CorreoDuplicadoException(
                    "Ya existe otro usuario registrado con ese nombre y correo electronico.");
        }

        try {
            return usuarioMapper.aRespuesta(persistir(usuario));
        } catch (DataIntegrityViolationException excepcion) {
            bitacora.warn("La restriccion de unicidad rechazo la actualizacion del correo solicitado.");
            throw new CorreoDuplicadoException();
        }
    }

    // ------------------------------------------------------------------
    // 2.5 Baja
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public ResultadoEliminacion eliminar(UsuarioEliminacionRequest solicitud) {
        Usuario usuario = localizar(solicitud.getNombre(), solicitud.getCorreo());

        if (solicitud.esEliminacionFisica()) {
            UsuarioResponse instantanea = usuarioMapper.aRespuesta(usuario);
            usuarioRepository.delete(usuario);
            bitacora.info("Eliminacion fisica aplicada -> {}", usuario.descripcionAuditoria());
            return new ResultadoEliminacion(instantanea, true);
        }

        aplicarEstado(usuario, Boolean.FALSE);
        UsuarioResponse respuesta = usuarioMapper.aRespuesta(persistir(usuario));
        bitacora.info("Baja logica aplicada -> {}", usuario.descripcionAuditoria());
        return new ResultadoEliminacion(respuesta, false);
    }

    // ------------------------------------------------------------------
    // Apoyo interno
    // ------------------------------------------------------------------

    /** Localiza al usuario por la llave compuesta o lanza la excepcion de negocio. */
    private Usuario localizar(String nombre, String correo) {
        return usuarioRepository
                .buscarPorLlaveCompuesta(nombre.trim(), usuarioMapper.normalizarCorreo(correo))
                .orElseThrow(UsuarioNoEncontradoException::new);
    }

    /**
     * Devuelve el valor recibido o rechaza el null explicito.
     * Un Optional vacio significa que el cliente envio el campo con valor null.
     */
    private <T> T valorObligatorio(Optional<T> valor, String campo) {
        return valor.orElseThrow(() -> new CampoNoAnulableException(campo));
    }

    /** Centraliza el cambio de estado para mantener coherente la fecha de baja. */
    private void aplicarEstado(Usuario usuario, Boolean nuevoEstado) {
        boolean estabaActivo = Boolean.TRUE.equals(usuario.getActivo());
        boolean quedaActivo = Boolean.TRUE.equals(nuevoEstado);

        usuario.setActivo(quedaActivo);

        if (estabaActivo && !quedaActivo) {
            usuario.setFechaBaja(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        } else if (quedaActivo) {
            usuario.setFechaBaja(null);
        }
    }

    /** Arma el Pageable a partir del bloque opcional de paginacion y ordenamiento. */
    private Pageable armarPaginacion(UsuarioBusquedaRequest criterios) {
        int numeroPagina = 0;
        int tamanio = propiedadesPaginacion.tamanioPorDefectoSeguro();

        PaginacionRequest paginacion = criterios == null ? null : criterios.getPaginacion();
        if (paginacion != null) {
            numeroPagina = paginacion.getPagina() == null ? 0 : paginacion.getPagina();
            tamanio = propiedadesPaginacion.ajustar(paginacion.getTamanio());
        }

        return PageRequest.of(numeroPagina, tamanio, armarOrdenamiento(criterios == null ? null : criterios.getOrden()));
    }

    /** Traduce el bloque de ordenamiento validando los nombres de campo recibidos. */
    private Sort armarOrdenamiento(OrdenRequest orden) {
        if (orden == null || orden.getCampos() == null || orden.getCampos().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        Sort.Direction direccion = Boolean.FALSE.equals(orden.getAscendente())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        List<Sort.Order> ordenes = new ArrayList<>();
        for (String campo : orden.getCampos()) {
            String limpio = campo == null ? "" : campo.trim();
            if (!CAMPOS_ORDENABLES.contains(limpio)) {
                throw new CriterioInvalidoException(
                        "El campo de ordenamiento '%s' no es valido. Campos permitidos: %s."
                                .formatted(limpio, String.join(", ", CAMPOS_ORDENABLES)));
            }
            ordenes.add(new Sort.Order(direccion, limpio));
        }
        return Sort.by(ordenes);
    }

    /** Convierte la pagina de entidades en la respuesta paginada de DTOs. */
    private RespuestaPaginada<UsuarioResponse> armarRespuesta(String mensaje, Page<Usuario> pagina) {
        List<UsuarioResponse> datos = usuarioMapper.aRespuestas(pagina.getContent());

        MetadatosPaginacion metadatos = new MetadatosPaginacion(
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getNumberOfElements(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );

        return new RespuestaPaginada<>(mensaje, datos, metadatos);
    }
}
