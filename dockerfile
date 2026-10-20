# Etapa 1: Compilación de la aplicación con Maven integrado
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Instalar Maven en el contenedor de forma limpia
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copiar únicamente el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el paquete JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución con JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el JAR generado desde la primera etapa (asegúrate de que el nombre coincida con tu artifactId y versión)
COPY --from=builder /app/target/administracion-usuarios-api-1.0.0.jar app.jar

# Exponer el puerto de Spring Boot
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]