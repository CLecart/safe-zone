FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="SafeZone Team"
LABEL description="SafeZone User Service"

ARG JAR_FILE=target/*.jar
ARG APP_USER=appuser
ARG APP_GROUP=appgroup

RUN addgroup -S ${APP_GROUP} && adduser -S ${APP_USER} -G ${APP_GROUP}

WORKDIR /app

COPY ${JAR_FILE} app.jar

RUN chown -R ${APP_USER}:${APP_GROUP} /app

USER ${APP_USER}

EXPOSE 8083

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
