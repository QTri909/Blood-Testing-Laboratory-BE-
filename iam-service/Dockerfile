FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy iam-service jar
COPY iam-service/target/iam-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
