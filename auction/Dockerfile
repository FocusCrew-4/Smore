FROM amazoncorretto:21

WORKDIR /app

# DocDB CA 번들 복사
COPY certs/global-bundle.pem /tmp/global-bundle.pem

# PEM 분해
RUN awk '/BEGIN CERTIFICATE/{i++}{print > "/tmp/docdb-cert-"i".pem"}' /tmp/global-bundle.pem

# JVM 기본 cacerts에 DocDB CA 추가
RUN for f in /tmp/docdb-cert-*.pem; do \
    keytool -importcert \
      -alias "$(basename $f)" \
      -file "$f" \
      -keystore "$JAVA_HOME/lib/security/cacerts" \
      -storepass changeit \
      -noprompt; \
  done

# 애플리케이션 JAR
COPY build/libs/*.jar auction.jar

ENTRYPOINT ["java", "-jar", "/app/auction.jar"]
