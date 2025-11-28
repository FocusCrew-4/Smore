# auction - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar auction.jar

ENTRYPOINT ["java", "-jar", "/app/auction.jar"]