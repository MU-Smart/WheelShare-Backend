# ----- Build Stage -----
FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src src

# Build the project
RUN mvn package

# ----- Run Stage -----
FROM openjdk:17-jdk-alpine AS runtime

WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/target/my-path-back-end-*.jar app.jar

# Copy the map download script
COPY downloadMap.sh .

# Make map download script executable
RUN chmod +x downloadMap.sh

# Create a cron job to run the map download script every 5 minutes
RUN echo "*/5 * * * * /app/downloadMap.sh" > /etc/crontabs/root

# Expose API port
EXPOSE 8081

# Run the jar file
CMD ["/bin/sh", "-c", "/app/downloadMap.sh && crond && exec java -jar app.jar"]
