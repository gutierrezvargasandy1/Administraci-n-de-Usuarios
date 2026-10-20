package com.utng.usuarios.exception;

import com.utng.usuarios.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES.
 *
 * Concentra en un solo lugar la traduccion de cualquier error a una respuesta
 * HTTP con formato unico. Gracias a esta clase los controladores no contienen
 * un solo bloque try-catch ni arman respuestas de error por su cuenta.
 *
 * El manejador de {@link NegocioException} es el ejemplo mas claro de
 * POLIMORFISMO del proyecto: atiende por igual a UsuarioNoEncontradoException,
 * CorreoDuplicadoException, CriterioInvalidoException y CampoNoAnulableException
 * porque invoca getCodigo() y getEstado() sobre la referencia abstracta, y cada
 * subclase responde con sus propios valores.
 *
 * Ninguna respuesta expone sentencias SQL, nombres internos de tablas ni trazas
 * de ejecucion: esa informacion solo se escribe en la bitacora del servidor.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger BITACORA = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Errores de Bean Validation producidos por @Valid sobre los Request DTO.
     * La respuesta identifica cada campo con problemas y su mensaje.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> manejarValidacion(MethodArgumentNotValidException excepcion,
                                                              HttpServletRequest peticion) {
        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error : excepcion.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(normalizarCampo(error.getField()), error.getDefaultMessage());
        }
        for (ObjectError error : excepcion.getBindingResult().getGlobalErrors()) {
            errores.putIfAbsent(error.getObjectName(), error.getDefaultMessage());
        }

        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "DATOS_INVALIDOS",
                "Verifique la informacion capturada e intente nuevamente.",
                errores,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    /**
     * Cuerpo JSON ausente o mal formado (por ejemplo, un tipo de dato incorrecto).
     * No se devuelve el detalle interno del analizador.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> manejarJsonIlegible(HttpMessageNotReadableException excepcion,
                                                                HttpServletRequest peticion) {
        BITACORA.warn("Cuerpo de la peticion ilegible en {}: {}", peticion.getRequestURI(), excepcion.getMessage());

        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "JSON_INVALIDO",
                "El cuerpo de la peticion no es un JSON valido o algun campo tiene un tipo incorrecto.",
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    /**
     * Manejo POLIMORFICO de todas las excepciones de negocio.
     * Un unico metodo resuelve 404, 409 y 400 porque cada subclase aporta su
     * propio codigo y su propio estado HTTP.
     */
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ApiErrorResponse> manejarNegocio(NegocioException excepcion,
                                                           HttpServletRequest peticion) {
        Map<String, String> errores = null;
        if (excepcion instanceof CampoNoAnulableException campoNoAnulable) {
            errores = Map.of(campoNoAnulable.getCampo(), excepcion.getMessage());
        }

        BITACORA.info("Regla de negocio aplicada [{}] en {}: {}",
                excepcion.getCodigo(), peticion.getRequestURI(), excepcion.getMessage());

        ApiErrorResponse cuerpo = new ApiErrorResponse(
                excepcion.getCodigo(),
                excepcion.getMessage(),
                errores,
                peticion.getRequestURI());

        return ResponseEntity.status(excepcion.getEstado()).body(cuerpo);
    }

    /**
     * Violaciones de las restricciones de integridad de la base de datos que no
     * fueron interceptadas antes por una regla de negocio.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> manejarIntegridad(DataIntegrityViolationException excepcion,
                                                              HttpServletRequest peticion) {
        BITACORA.error("Violacion de integridad en {}", peticion.getRequestURI(), excepcion);

        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "CONFLICTO_INTEGRIDAD",
                "La operacion no pudo completarse porque entra en conflicto con la informacion existente.",
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }

    /** Metodo HTTP no permitido para la ruta solicitada. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException excepcion,
                                                                     HttpServletRequest peticion) {
        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "METODO_NO_PERMITIDO",
                "El metodo %s no esta disponible para esta ruta.".formatted(excepcion.getMethod()),
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(cuerpo);
    }

    /** Tipo de contenido no soportado: la API solo acepta application/json. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> manejarMediaType(HttpMediaTypeNotSupportedException excepcion,
                                                             HttpServletRequest peticion) {
        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "TIPO_CONTENIDO_NO_SOPORTADO",
                "La peticion debe enviarse con Content-Type: application/json.",
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(cuerpo);
    }

    /** Ruta inexistente: se responde 404 sin revelar la estructura interna de la aplicacion. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> manejarRutaInexistente(NoResourceFoundException excepcion,
                                                                   HttpServletRequest peticion) {
        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "RECURSO_NO_ENCONTRADO",
                "La ruta solicitada no existe en esta API.",
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    /**
     * Ultimo recurso para errores no previstos. Se registra la traza completa en
     * la bitacora del servidor y se devuelve un mensaje controlado al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarInesperado(Exception excepcion,
                                                              HttpServletRequest peticion) {
        BITACORA.error("Error inesperado en {}", peticion.getRequestURI(), excepcion);

        ApiErrorResponse cuerpo = new ApiErrorResponse(
                "ERROR_INTERNO",
                "Ocurrio un error inesperado. Intente nuevamente mas tarde.",
                null,
                peticion.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo);
    }

    /**
     * Limpia los sufijos que Bean Validation agrega al validar elementos dentro
     * de contenedores, por ejemplo "telefono[]" en los campos Optional.
     */
    private String normalizarCampo(String campo) {
        return campo == null ? "desconocido" : campo.replace("[]", "").replace("<value>", "");
    }
}
