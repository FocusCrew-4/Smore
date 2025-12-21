# auction - Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY certs/global-bundle.pem /app/global-bundle.pem

RUN keytool -importkeystore \
  -srckeystore /app/global-bundle.pem \
  -srcstoretype PEM \
  -srcstorepass "" \
  -destkeystore /app/docdb-truststore.p12 \
  -deststoretype PKCS12 \
  -storepass changeit \
  -noprompt

# 3. JVM에 trustStore 지정.
ENV JAVA_TOOL_OPTIONS="\
-Djavax.net.ssl.trustStore=/app/docdb-truststore.p12 \
-Djavax.net.ssl.trustStoreType=PKCS12 \
-Djavax.net.ssl.trustStorePassword=changeit"

COPY build/libs/*.jar auction.jar

ENTRYPOINT ["java", "-jar", "/app/auction.jar"]
