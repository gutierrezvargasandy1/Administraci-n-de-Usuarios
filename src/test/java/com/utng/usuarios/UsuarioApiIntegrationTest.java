package com.utng.usuarios;

import com.utng.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion de los endpoints de la API.
 *
 * Cubren los casos exitosos y los casos de error exigidos por el instrumento de
 * evaluacion: validaciones, correo duplicado, usuario inexistente, actualizacion
 * parcial, distincion entre campo omitido y null explicito, baja logica y
 * busqueda dinamica.
 *
 * Se ejecutan con el perfil "test" sobre una base de datos en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioApiIntegrationTest {

    private static final String RUTA = "/api/v1/usuarios";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limpiarTabla() {
        usuarioRepository.deleteAll();
    }

    private void registrarJuan() throws Exception {
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com",
                                  "telefono": "4771234567"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios registra el usuario y responde 201")
    void registraUsuario() throws Exception {
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com",
                                  "telefono": "4771234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente."))
                .andExpect(jsonPath("$.datos.id").isNumber())
                .andExpect(jsonPath("$.datos.correo").value("juan.perez@correo.com"))
                .andExpect(jsonPath("$.datos.activo").value(true))
                .andExpect(jsonPath("$.datos.fechaCreacion").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios rechaza datos invalidos con 400 y detalle por campo")
    void rechazaDatosInvalidos() throws Exception {
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "   ",
                                  "correo": "correo-sin-formato",
                                  "telefono": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
                .andExpect(jsonPath("$.errores.nombre").isNotEmpty())
                .andExpect(jsonPath("$.errores.correo").isNotEmpty())
                .andExpect(jsonPath("$.errores.telefono").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios rechaza el correo duplicado con 409")
    void rechazaCorreoDuplicado() throws Exception {
        registrarJuan();

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Otro Nombre",
                                  "correo": "juan.perez@correo.com",
                                  "telefono": "4779999999"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CORREO_DUPLICADO"));
    }

    @Test
    @DisplayName("POST /api/v1/usuarios/consulta localiza por llave compuesta y responde 404 si no existe")
    void consultaPorLlaveCompuesta() throws Exception {
        registrarJuan();

        mockMvc.perform(post(RUTA + "/consulta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.telefono").value("4771234567"));

        mockMvc.perform(post(RUTA + "/consulta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "otro.correo@correo.com"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("USUARIO_NO_ENCONTRADO"));
    }

    @Test
    @DisplayName("PATCH /api/v1/usuarios/actualizacion modifica solo los campos enviados")
    void actualizacionParcial() throws Exception {
        registrarJuan();

        mockMvc.perform(patch(RUTA + "/actualizacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com",
                                  "telefono": "4779876543"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.telefono").value("4779876543"))
                .andExpect(jsonPath("$.datos.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.datos.correo").value("juan.perez@correo.com"));
    }

    @Test
    @DisplayName("PATCH rechaza el null explicito sobre un campo obligatorio")
    void rechazaNullExplicito() throws Exception {
        registrarJuan();

        mockMvc.perform(patch(RUTA + "/actualizacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com",
                                  "telefono": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CAMPO_NO_ANULABLE"));
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/eliminacion aplica baja logica y conserva el registro")
    void bajaLogica() throws Exception {
        registrarJuan();

        mockMvc.perform(delete(RUTA + "/eliminacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Perez",
                                  "correo": "juan.perez@correo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.activo").value(false));

        org.junit.jupiter.api.Assertions.assertEquals(1, usuarioRepository.count());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios/busqueda combina criterios en una sola consulta")
    void busquedaDinamica() throws Exception {
        registrarJuan();

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Maria Lopez",
                                  "correo": "maria.lopez@correo.com",
                                  "telefono": "4775554433",
                                  "activo": false
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post(RUTA + "/busqueda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "juan",
                                  "activo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1))
                .andExpect(jsonPath("$.datos[0].correo").value("juan.perez@correo.com"))
                .andExpect(jsonPath("$.paginacion.totalElementos").value(1));

        mockMvc.perform(post(RUTA + "/busqueda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "correo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paginacion.totalElementos").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/usuarios devuelve el listado paginado con estructura consistente")
    void listadoPaginado() throws Exception {
        registrarJuan();

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").isNotEmpty())
                .andExpect(jsonPath("$.datos").isArray())
                .andExpect(jsonPath("$.paginacion.pagina").value(0))
                .andExpect(jsonPath("$.paginacion.totalElementos").value(1));
    }
}
