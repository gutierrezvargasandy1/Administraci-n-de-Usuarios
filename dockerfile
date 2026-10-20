# etapa 1: compilacion con java 21 y maven wrapper
from eclipse-temurin:21-jdk-jammy as builder
workdir /app

# copiar archivos de configuracion de maven primero para aprovechar la cache de capas
copy .mvn/ .mvn/
copy mvnw pom.xml ./

# dar permisos de ejecucion al wrapper
run chmod +x mvnw

# descargar dependencias previamente
run ./mvnw dependency:go-offline -b

# copiar el código fuente de la aplicacion
copy src ./src

# compilar y generar el jar omitiendo los tests en el build
run ./mvnw clean package -Dskiptests

# etapa 2: imagen ligera de ejecucion con jre 21
from eclipse-temurin:21-jre-jammy
workdir /app

# copiar el jar generado desde la etapa de compilacion
copy --from=builder /app/target/administracion-usuarios-api-1.0.0.jar app.jar

# exponer el puerto por defecto de spring boot
expose 8080

# comando para iniciar la aplicacion
entrypoint ["java", "-jar", "app.jar"]