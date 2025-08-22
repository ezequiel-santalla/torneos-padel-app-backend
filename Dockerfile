# Etapa de construcción
FROM eclipse-temurin:21-jdk-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar solo los archivos necesarios para mejor cache de Docker
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Construir la aplicación sin tests con encoding UTF-8
RUN ./mvnw clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8 -Dproject.reporting.outputEncoding=UTF-8

# Etapa de producción
FROM eclipse-temurin:21-jre-alpine

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Variables de entorno para optimizar JVM en contenedores
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=80"

# Crear usuario no-root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]