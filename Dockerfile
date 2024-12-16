# Use an OpenJDK base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY target/quick-shop-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
