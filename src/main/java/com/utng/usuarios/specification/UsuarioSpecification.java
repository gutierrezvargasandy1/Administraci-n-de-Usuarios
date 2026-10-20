package com.utng.usuarios.specification;

import com.utng.usuarios.dto.request.RangoFechasRequest;
import com.utng.usuarios.dto.request.UsuarioBusquedaRequest;
import com.utng.usuarios.entity.Usuario;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTACION de la interfaz {@link Specification} de Spring Data JPA.
 *
 * Construye dinamicamente el WHERE de la consulta a partir de los criterios
 * recibidos: cada criterio presente agrega un predicado y cada criterio omitido
 * simplemente no participa. Todos los predicados se combinan con AND.
 *
 * Reglas de coincidencia definidas para el proyecto:
 * - nombre   : coincidencia PARCIAL, insensible a mayusculas;
 * - correo   : coincidencia PARCIAL, insensible a mayusculas;
 * - telefono : coincidencia PARCIAL;
 * - activo   : coincidencia EXACTA;
 * - fechaCreacion: rango inclusivo, con cualquiera de sus dos extremos opcional.
 *
 * Los valores del cliente jamas se concatenan dentro de una sentencia SQL: el
 * API de criterios los transporta como parametros enlazados, y los comodines
 * de LIKE que el usuario pudiera escribir se escapan antes de armar el patron.
 */
public class UsuarioSpecification implements Specification<Usuario> {

    private static final long serialVersionUID = 1L;

    /** Caracter de escape para los comodines de LIKE. */
    private static final char ESCAPE = '\\';

    private final String nombre;
    private final String correo;
    private final String telefono;
    private final Boolean activo;
    private final LocalDateTime creadoDesde;
    private final LocalDateTime creadoHasta;

    private UsuarioSpecification(String nombre, String correo, String telefono, Boolean activo,
                                 LocalDateTime creadoDesde, LocalDateTime creadoHasta) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.activo = activo;
        this.creadoDesde = creadoDesde;
        this.creadoHasta = creadoHasta;
    }

    /** Fabrica la especificacion a partir del Request DTO de busqueda. */
    public static UsuarioSpecification conCriterios(UsuarioBusquedaRequest criterios) {
        if (criterios == null) {
            return new UsuarioSpecification(null, null, null, null, null, null);
        }
        RangoFechasRequest rango = criterios.getRangoFechaCreacion();
        return new UsuarioSpecification(
                criterios.getNombre(),
                criterios.getCorreo(),
                criterios.getTelefono(),
                criterios.getActivo(),
                rango == null ? null : rango.getDesde(),
                rango == null ? null : rango.getHasta()
        );
    }

    /** true cuando el cliente no proporciono ningun filtro. */
    public boolean sinCriterios() {
        return vacio(nombre) && vacio(correo) && vacio(telefono)
                && activo == null && creadoDesde == null && creadoHasta == null;
    }

    @Override
    public Predicate toPredicate(Root<Usuario> raiz, CriteriaQuery<?> consulta, CriteriaBuilder constructor) {
        List<Predicate> predicados = new ArrayList<>();

        if (!vacio(nombre)) {
            predicados.add(constructor.like(
                    constructor.lower(raiz.get("nombre")), patronParcial(nombre), ESCAPE));
        }
        if (!vacio(correo)) {
            predicados.add(constructor.like(
                    constructor.lower(raiz.get("correo")), patronParcial(correo), ESCAPE));
        }
        if (!vacio(telefono)) {
            predicados.add(constructor.like(
                    raiz.get("telefono"), patronParcial(telefono), ESCAPE));
        }
        if (activo != null) {
            predicados.add(constructor.equal(raiz.get("activo"), activo));
        }
        if (creadoDesde != null) {
            predicados.add(constructor.greaterThanOrEqualTo(raiz.get("fechaCreacion"), creadoDesde));
        }
        if (creadoHasta != null) {
            predicados.add(constructor.lessThanOrEqualTo(raiz.get("fechaCreacion"), creadoHasta));
        }

        if (predicados.isEmpty()) {
            // Sin criterios la consulta no filtra, pero el servicio siempre la pagina.
            return constructor.conjunction();
        }
        return constructor.and(predicados.toArray(new Predicate[0]));
    }

    /** Construye el patron %valor% neutralizando los comodines escritos por el cliente. */
    private String patronParcial(String valor) {
        String limpio = valor.trim()
                .replace(String.valueOf(ESCAPE), ESCAPE + "\\")
                .replace("%", ESCAPE + "%")
                .replace("_", ESCAPE + "_");
        return "%" + limpio.toLowerCase() + "%";
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
