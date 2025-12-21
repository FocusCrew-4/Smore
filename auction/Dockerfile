# auction - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY certs/global-bundle.pem /app/global-bundle.pem

RUN keytool -importcert \
    -file /app/global-bundle.pem \
    -alias docdb-ca \
    -keystore /app/docdb-truststore.jks \
    -storepass changeit \
    -noprompt

# 3. JVM에 trustStore 지정.
ENV JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStore=/app/docdb-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"

COPY build/libs/*.jar auction.jar

ENTRYPOINT ["java", "-jar", "/app/auction.jar"]
