# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy toàn bộ project (có parent pom.xml)
COPY ../ .

# Debug (tuỳ chọn)
RUN echo "📁 Root folder:" && ls -la /app && \
    echo "📁 Modules:" && ls -la /app/common && ls -la /app/iam-service

# ✅ Build riêng IAM và common
RUN mvn -pl iam-service -am clean install -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/iam-service/target/iam-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
