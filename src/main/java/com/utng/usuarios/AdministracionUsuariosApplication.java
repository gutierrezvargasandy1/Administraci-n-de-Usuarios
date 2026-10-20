package com.utng.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de la API REST para la administracion de usuarios.
 *
 * Asignatura : Introduccion a Aplicaciones Web Progresivas (PWA) - Unidad I
 * Institucion: Universidad Tecnologica del Norte de Guanajuato (UTNG)
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AdministracionUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdministracionUsuariosApplication.class, args);
    }
}
