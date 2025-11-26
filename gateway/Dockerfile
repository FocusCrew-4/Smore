# gateway - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

#ENV EUREKA_HOST=eureka-server
#ENV SPRING_PROFILES_ACTIVE=prod

COPY build/libs/*.jar gateway.jar

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]