package com.utng.usuarios.mapper;

import com.utng.usuarios.dto.request.UsuarioCreateRequest;
import com.utng.usuarios.dto.response.UsuarioResponse;
import com.utng.usuarios.entity.Usuario;
import org.springframework.stereotype.Component;

/**
 * IMPLEMENTACION de {@link Mapeador} para la entidad Usuario.
 *
 * Es el unico punto del proyecto donde la entidad JPA se convierte en el DTO que
 * sale hacia el cliente. El identificador y la fecha de creacion solo viajan en
 * esta direccion: entidad -> respuesta, nunca al reves.
 */
@Component
public class UsuarioMapper implements Mapeador<Usuario, UsuarioResponse> {

    @Override
    public UsuarioResponse aRespuesta(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }

    /**
     * Construye la entidad a partir del Request DTO de alta.
     * No copia el id ni la fecha de creacion: son responsabilidad del servidor.
     */
    public Usuario aEntidad(UsuarioCreateRequest solicitud) {
        Usuario usuario = new Usuario();
        usuario.setNombre(solicitud.getNombre().trim());
        usuario.setCorreo(normalizarCorreo(solicitud.getCorreo()));
        usuario.setTelefono(solicitud.getTelefono().trim());
        usuario.setActivo(solicitud.getActivo() == null ? Boolean.TRUE : solicitud.getActivo());
        return usuario;
    }

    /** Los correos se almacenan y se comparan en minusculas y sin espacios sobrantes. */
    public String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }
}
