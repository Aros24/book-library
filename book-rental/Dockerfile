# Use an official Java runtime as a parent image for building
FROM openjdk:21-jdk-slim as builder

# Install Maven in the builder image
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build file (pom.xml) into the container
COPY pom.xml .

# Copy the project files into the container
COPY src ./src

# Run tests and package the application (this will create the .jar file in the target folder)
RUN mvn clean verify

# Use a full JDK image for running the application
FROM openjdk:21-jdk-slim

# Install netcat (openbsd version) for the wait-for-it.sh script
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the builder image
COPY --from=builder /app/target/book-rental-*.jar /app/book-rental.jar

# Copy the wait-for-it.sh script into the container
COPY wait-for-it.sh /app/wait-for-it.sh

# Ensure the wait-for-it.sh script is executable
RUN chmod +x /app/wait-for-it.sh

# Command to wait for the database to be ready and then start the application
ENTRYPOINT ["/app/wait-for-it.sh", "book-rental-db:3306", "--timeout=50", "--", "java", "-jar", "/app/book-rental.jar"]