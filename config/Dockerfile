# config - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar config.jar

ENTRYPOINT ["java", "-jar", "/app/config.jar"]