# Imagem JVM da api-2 (Quarkus), porta HTTP 8081.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /work
RUN useradd -r -u 185 quarkus && chown quarkus:quarkus /work
COPY --from=build --chown=quarkus:quarkus /app/target/quarkus-app/ ./
USER quarkus
ENV JAVA_TOOL_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8081"
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
