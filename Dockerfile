FROM gradle:8-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle /app/

RUN gradle dependencies --no-daemon

COPY src /app/src
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
