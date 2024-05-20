# Use the official Maven image to build the project
FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use the official OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Install ffmpeg
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/media-convertor-api.jar app.jar

# Specify the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
