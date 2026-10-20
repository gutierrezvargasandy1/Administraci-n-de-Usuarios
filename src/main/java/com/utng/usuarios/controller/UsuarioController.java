package com.utng.usuarios.controller;

import com.utng.usuarios.dto.request.UsuarioBusquedaRequest;
import com.utng.usuarios.dto.request.UsuarioConsultaRequest;
import com.utng.usuarios.dto.request.UsuarioCreateRequest;
import com.utng.usuarios.dto.request.UsuarioEliminacionRequest;
import com.utng.usuarios.dto.request.UsuarioUpdateRequest;
import com.utng.usuarios.dto.response.ApiErrorResponse;
import com.utng.usuarios.dto.response.RespuestaApi;
import com.utng.usuarios.dto.response.RespuestaPaginada;
import com.utng.usuarios.dto.response.UsuarioResponse;
import com.utng.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de la administracion de usuarios.
 *
 * RESTRICCIONES DE DISENO RESPETADAS EN TODO EL CONTROLADOR
 * - No se utiliza @PathVariable en ningun endpoint.
 * - No se utiliza @RequestParam para recibir identificadores, filtros ni criterios.
 * - Toda la informacion entra por el cuerpo de la peticion con @RequestBody.
 * - Todos los cuerpos recibidos se validan con @Valid.
 * - No hay logica de negocio ni bloques try-catch: el controlador solo recibe,
 *   delega en la capa de servicio y traduce el resultado a una respuesta HTTP.
 * - No se devuelve ninguna entidad JPA: siempre se responde con Response DTOs.
 */
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Usuarios", description = "Alta, baja, consulta, modificacion y busqueda dinamica de usuarios.")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ------------------------------------------------------------------
    // 2.1 Crear usuario
    // ------------------------------------------------------------------
    @Operation(
            summary = "Registrar un usuario",
            description = """
                    Da de alta un usuario nuevo. El identificador y la fecha de creacion
                    los asigna el servidor y no pueden enviarse en la peticion.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos invalidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El correo ya se encuentra registrado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespuestaApi<UsuarioResponse>> crear(
            @Valid @RequestBody UsuarioCreateRequest solicitud) {

        UsuarioResponse usuario = usuarioService.crear(solicitud);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaApi.de("Usuario registrado correctamente.", usuario));
    }

    // ------------------------------------------------------------------
    // 2.2 Consultar usuarios
    // ------------------------------------------------------------------
    @Operation(
            summary = "Listar usuarios",
            description = """
                    Devuelve la primera pagina del listado de usuarios con el tamanio
                    configurado en el servidor (app.paginacion.tamanio-por-defecto).
                    Para recorrer el resto de las paginas o aplicar filtros se utiliza
                    POST /api/v1/usuarios/busqueda, que recibe la paginacion y los
                    criterios dentro de un Request DTO, sin usar @RequestParam.
                    """)
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.")
    @GetMapping
    public ResponseEntity<RespuestaPaginada<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    // ------------------------------------------------------------------
    // 2.3 Consultar usuario mediante llave compuesta
    // ------------------------------------------------------------------
    @Operation(
            summary = "Consultar un usuario por llave compuesta",
            description = """
                    Localiza un usuario cuyo nombre Y correo electronico coincidan con
                    los valores proporcionados. No existe consulta por identificador.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado."),
            @ApiResponse(responseCode = "400", description = "Datos invalidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con esa llave compuesta.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping(value = "/consulta", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponse> consultar(
            @Valid @RequestBody UsuarioConsultaRequest solicitud) {

        return ResponseEntity.ok(usuarioService.consultarPorLlaveCompuesta(solicitud));
    }

    // ------------------------------------------------------------------
    // 7. Busqueda dinamica
    // ------------------------------------------------------------------
    @Operation(
            summary = "Busqueda dinamica de usuarios",
            description = """
                    Combina cualquier subconjunto de criterios (nombre, correo, telefono,
                    estado y rango de fecha de creacion) en una sola consulta construida
                    con Specification. Los criterios omitidos se excluyen del WHERE.
                    Tambien recibe la paginacion y el ordenamiento dentro del mismo DTO.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busqueda ejecutada correctamente."),
            @ApiResponse(responseCode = "400", description = "Criterios invalidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping(value = "/busqueda", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespuestaPaginada<UsuarioResponse>> buscar(
            @Valid @RequestBody UsuarioBusquedaRequest criterios) {

        return ResponseEntity.ok(usuarioService.buscar(criterios));
    }

    // ------------------------------------------------------------------
    // 2.4 Actualizar usuario
    // ------------------------------------------------------------------
    @Operation(
            summary = "Actualizacion parcial de un usuario",
            description = """
                    Modifica unicamente los campos recibidos. El usuario se localiza con la
                    llave compuesta nombre + correo; para cambiar esos mismos datos se
                    utilizan los campos nuevoNombre y nuevoCorreo.
                    Un campo omitido no se modifica; un campo enviado explicitamente con
                    null sobre un dato obligatorio se rechaza con CAMPO_NO_ANULABLE.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos o criterios invalidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con esa llave compuesta.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El correo ya pertenece a otro usuario.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping(value = "/actualizacion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespuestaApi<UsuarioResponse>> actualizar(
            @Valid @RequestBody UsuarioUpdateRequest solicitud) {

        UsuarioResponse usuario = usuarioService.actualizar(solicitud);
        return ResponseEntity.ok(RespuestaApi.de("Usuario actualizado correctamente.", usuario));
    }

    // ------------------------------------------------------------------
    // 2.5 Eliminar usuario
    // ------------------------------------------------------------------
    @Operation(
            summary = "Baja de un usuario",
            description = """
                    Da de baja al usuario localizado por su llave compuesta.
                    La baja es LOGICA de manera predeterminada: el registro se conserva y
                    se marca activo = false con su fecha de baja. La eliminacion fisica
                    solo se ejecuta cuando la peticion incluye eliminacionFisica = true.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario dado de baja correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos invalidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con esa llave compuesta.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping(value = "/eliminacion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespuestaApi<UsuarioResponse>> eliminar(
            @Valid @RequestBody UsuarioEliminacionRequest solicitud) {

        UsuarioService.ResultadoEliminacion resultado = usuarioService.eliminar(solicitud);

        String mensaje = resultado.fisica()
                ? "Usuario eliminado definitivamente."
                : "Usuario dado de baja correctamente.";

        return ResponseEntity.ok(RespuestaApi.de(mensaje, resultado.usuario()));
    }
}
