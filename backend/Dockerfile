# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/gamelive-backend-1.0.0.jar /app/gamelive-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/gamelive-backend.jar"]
