# Build stage
FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]