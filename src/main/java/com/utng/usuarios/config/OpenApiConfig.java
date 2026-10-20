package com.utng.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion de la documentacion OpenAPI 3 / Swagger UI.
 *
 * Interfaz grafica: http://localhost:8080/swagger-ui.html
 * Contrato JSON   : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApiAdministracionUsuarios() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST para la Administracion de Usuarios")
                        .version("1.0.0")
                        .description("""
                                API REST desarrollada con Java 21, Spring Boot 3.3.x, Spring Data JPA y PostgreSQL.

                                Caracteristicas principales:
                                - Todos los datos se reciben mediante Request DTO con @RequestBody.
                                - No se utiliza @PathVariable ni @RequestParam en ningun endpoint.
                                - El usuario se localiza mediante la llave compuesta nombre + correo.
                                - Validaciones declarativas con Bean Validation y ejecucion con @Valid.
                                - Manejo centralizado de errores con @RestControllerAdvice.
                                - Busquedas dinamicas con Specification y JpaSpecificationExecutor.
                                """)
                        .contact(new Contact()
                                .name("Jose Andres Gutierrez Vargas - No. de control 1223100413")
                                .email("1223100413@utng.edu.mx"))
                        .license(new License().name("Uso academico - UTNG")))
                .servers(List.of(new Server()
                        .url("https://administraci-n-de-usuarios.onrender.com")
                        .description("Entorno local de produccion")));
    }
}
