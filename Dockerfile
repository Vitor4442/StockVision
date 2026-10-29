FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace
ARG MODULE
COPY . .
RUN ./${MODULE}/mvnw -B -f ${MODULE}/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
ARG MODULE
COPY --from=builder /workspace/${MODULE}/target/*.jar /app/app.jar
USER spring:spring
EXPOSE 8081 8082 8083 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
