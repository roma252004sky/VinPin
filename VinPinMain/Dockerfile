FROM gradle:8.7-jdk17 AS build

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY src ./src
RUN gradle bootJar

FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY --from=build /app/build/libs/VinPinMain-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 80