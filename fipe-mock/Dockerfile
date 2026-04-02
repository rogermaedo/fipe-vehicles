# Build e execução do mock FIPE (WireMock); porta padrão 8090 (variável FIPE_MOCK_PORT).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/target/lib ./lib
COPY --from=build /build/target/fipe-mock-*.jar ./app.jar
ENV FIPE_MOCK_PORT=8090
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
