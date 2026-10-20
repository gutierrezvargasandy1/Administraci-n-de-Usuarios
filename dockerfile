# Etapa 1: Compilación con Java 21 y Maven instalado en el contenedor
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Instalar Maven en la imagen
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copiar el archivo pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir el archivo .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución con JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el .jar generado desde la etapa de compilación
COPY --from=builder /app/target/administracion-usuarios-api-1.0.0.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]