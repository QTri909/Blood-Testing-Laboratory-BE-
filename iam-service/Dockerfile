# Stage 1: Build project using Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy toàn bộ source code, bao gồm cả common và iam-service
COPY . .

# Build tất cả module và bỏ qua test
RUN mvn clean package -DskipTests

# Stage 2: Run iam-service
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy JAR của iam-service từ stage build
COPY --from=build /app/iam-service/target/iam-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
