FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -Dspring.profiles.active=test

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/user-service-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=America/Recife", "-jar", "app.jar"]
