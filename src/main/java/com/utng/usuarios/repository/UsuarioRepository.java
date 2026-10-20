package com.utng.usuarios.repository;

import com.utng.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * INTERFAZ de acceso a datos de la entidad Usuario.
 *
 * Hereda dos contratos de Spring Data JPA:
 * - JpaRepository: operaciones CRUD y paginacion;
 * - JpaSpecificationExecutor: ejecucion de consultas construidas dinamicamente
 *   con Specification, que es el mecanismo que resuelve la busqueda por
 *   cualquier combinacion de criterios sin multiplicar endpoints.
 *
 * Ninguna consulta concatena los valores recibidos del cliente: se utilizan
 * parametros con nombre y el API de criterios, por lo que no existe
 * posibilidad de inyeccion SQL.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    /**
     * Consulta por LLAVE COMPUESTA nombre + correo.
     * Equivale a: SELECT * FROM usuarios WHERE nombre = :nombre AND correo = :correo
     */
    @Query("SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.correo = :correo")
    Optional<Usuario> buscarPorLlaveCompuesta(@Param("nombre") String nombre, @Param("correo") String correo);

    /** Verifica la unicidad del correo antes de un alta. */
    boolean existsByCorreo(String correo);

    /** Verifica la unicidad del correo al actualizar, excluyendo al propio usuario. */
    boolean existsByCorreoAndIdNot(String correo, Long id);

    /** Verifica que la nueva llave compuesta no colisione con otro registro. */
    boolean existsByNombreAndCorreoAndIdNot(String nombre, String correo, Long id);
}
