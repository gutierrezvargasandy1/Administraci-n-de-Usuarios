# Etapa 1: Compilación con Java 21 y Maven Wrapper
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copiar archivos de configuración de Maven (sin la carpeta .mvn si no existe)
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/ || true

# Dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Descargar dependencias previamente
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente de la aplicación
COPY src ./src

# Compilar y generar el jar omitiendo los tests en el build
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución con JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el jar generado desde la etapa de compilación
COPY --from=builder /app/target/administracion-usuarios-api-1.0.0.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]