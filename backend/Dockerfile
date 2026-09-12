# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Dependencies first, so they stay cached while only sources change.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre AS runtime

# curl is only needed for the container healthcheck defined in compose.yaml
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --system --create-home --uid 1001 spring
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

# application.yaml writes to logs/fitness-app.log, relative to the working dir
RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
