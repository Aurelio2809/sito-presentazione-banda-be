# Stage 1: Build the backend with Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies to cache them
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# The pom.xml specifies <packaging>war</packaging>, but Spring Boot generates an executable war
COPY --from=build /app/target/*.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]
