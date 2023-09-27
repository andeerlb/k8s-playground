FROM openjdk:17-alpine
WORKDIR /app
ARG JAR_VERSION=0.0.1-SNAPSHOT
ARG SPRING_PROFILE_ACTIVE=prod

ENV JAR_VERSION=$JAR_VERSION \
    SPRING_PROFILE_ACTIVE=$SPRING_PROFILE_ACTIVE \
    JAVA_OPTS=""

COPY docker/entrypoint.sh /app/entrypoint.sh
COPY build/libs/app-$JAR_VERSION.jar /app/app-$JAR_VERSION.jar

RUN chmod +x /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]