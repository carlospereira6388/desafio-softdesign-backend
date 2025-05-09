FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src/ /app/src/
RUN mvn package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]