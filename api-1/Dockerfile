# Imagem JVM da api-1 (Quarkus). Gera o layout target/quarkus-app dentro do build.
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
ENV JAVA_TOOL_OPTIONS="-Dquarkus.http.host=0.0.0.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
