# payment - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

ENV EUREKA_HOST=eureka-server
ENV SPRING_PROFILES_ACTIVE=prod

COPY build/libs/*.jar payment.jar

ENTRYPOINT ["java", "-jar", "/app/payment.jar"]