# ============================================================================
# DOCKERFILE - BACKEND JAVA
# Imagen Docker multi-stage para el backend Spring Boot
# ============================================================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar POM y descargar dependencias (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar JAR compilado desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
