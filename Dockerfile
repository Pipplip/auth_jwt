# ---- Build Stage (optional wenn CI baut) ----
FROM gradle:9.3.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Optional: non-root user
RUN useradd -ms /bin/bash spring
USER spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]