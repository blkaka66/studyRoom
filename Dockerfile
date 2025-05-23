# Use the official OpenJDK image as the base image
FROM openjdk:17
#배포하려면 이거 지워야한다네
## Argument for the JAR file to be copied
#ARG JAR_FILE=build/libs/studyroom-0.0.1-SNAPSHOT-plain.jar

# Copy the JAR file into the image
COPY ${JAR_FILE} app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
