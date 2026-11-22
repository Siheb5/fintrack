# Multi-stage Docker build for the FinTrack backend.
#
# Stage 1 ("build") compiles the app into a runnable jar using Maven.
# Stage 2 ("run") copies ONLY that jar into a small Java runtime image, so the
# final image stays lean (no Maven, no source) — a production best practice.

# ---------- Stage 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy the Maven wrapper + project descriptor first and pre-download deps.
# (This layer is cached, so later code changes don't re-download everything.)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -B dependency:go-offline

# Copy the source and build the jar (skip tests here; CI already runs them).
COPY src src
RUN ./mvnw -B package -DskipTests

# ---------- Stage 2: run ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the build stage.
COPY --from=build /app/target/*.jar app.jar

# The app listens on 8080.
EXPOSE 8080

# Start the app. Cloud hosts set SPRING_PROFILES_ACTIVE=postgres and the DB_* env vars.
ENTRYPOINT ["java", "-jar", "app.jar"]
