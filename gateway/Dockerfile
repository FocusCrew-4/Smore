# gateway - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar gateway.jar

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]