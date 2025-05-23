## Use the official OpenJDK image as the base image
#FROM openjdk:17

## Argument for the JAR file to be copied
##ARG JAR_FILE=build/libs/studyroom-0.0.1-SNAPSHOT-plain.jar
#
## Copy the JAR file into the image
#COPY ${JAR_FILE} app.jar
#
## Command to run the application
#ENTRYPOINT ["java", "-jar", "/app.jar"]

##여기서부터 배포용 코드

# 1단계: Gradle 빌더 이미지에서 JAR 생성
FROM gradle:8.5-jdk17 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle bootJar

# 2단계: 최소한의 런타임 이미지로 실행
FROM openjdk:17-jdk-slim
COPY --from=builder /app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
