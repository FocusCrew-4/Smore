FROM amazoncorretto:21

WORKDIR /app
COPY certs/global-bundle.pem /app/global-bundle.pem

RUN keytool -importkeystore \
  -srckeystore /app/global-bundle.pem \
  -srcstoretype PEM \
  -destkeystore /app/docdb-truststore.p12 \
  -deststoretype PKCS12 \
  -storepass changeit \
  -noprompt

ENV JAVA_TOOL_OPTIONS="\
-Djavax.net.ssl.trustStore=/app/docdb-truststore.p12 \
-Djavax.net.ssl.trustStoreType=PKCS12 \
-Djavax.net.ssl.trustStorePassword=changeit"

COPY build/libs/*.jar auction.jar
ENTRYPOINT ["java", "-jar", "/app/auction.jar"]
