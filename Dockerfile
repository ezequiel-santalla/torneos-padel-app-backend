# Usar OpenJDK 17 como imagen base (puedes cambiar la versión según tu proyecto)
FROM openjdk:17-jdk-slim AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de tu aplicación
# Ajusta el nombre del archivo según tu proyecto
COPY . .

# No test
RUN ./mvnw clean package -DskipTests

# Etapa de producción
FROM openjdk:17-jre-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que tu aplicación escucha
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]