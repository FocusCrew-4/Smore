FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY certs/global-bundle.pem /app/global-bundle.pem

# PEM을 인증서 단위로 분리
RUN awk '
  /BEGIN CERTIFICATE/ {i++}
  {print > "/tmp/cert" i ".pem"}
' /app/global-bundle.pem

# 각 인증서를 truststore에 개별 import
RUN for f in /tmp/cert*.pem; do \
    keytool -importcert \
      -alias "$(basename $f)" \
      -file "$f" \
      -keystore /app/docdb-truststore.p12 \
      -storetype PKCS12 \
      -storepass changeit \
      -noprompt; \
  done

ENV JAVA_TOOL_OPTIONS="\
-Djavax.net.ssl.trustStore=/app/docdb-truststore.p12 \
-Djavax.net.ssl.trustStoreType=PKCS12 \
-Djavax.net.ssl.trustStorePassword=changeit"

COPY build/libs/*.jar auction.jar
ENTRYPOINT ["java", "-jar", "/app/auction.jar"]
