# Image with gradle on alpine, mark as build
FROM gradle:7.4-jdk11-alpine AS build
# Copy in contaier files
COPY --chown=gradle:gradle . /home/gradle/src
# Working directory for new instruction
WORKDIR /home/gradle/src
# Run and add new slice
RUN gradle build

# Image with jre
FROM openjdk:11-jre-slim
# Need to open 8080 port
EXPOSE 8080:8080
# Create new directory for jar
RUN mkdir /app
# Move jar to directory
COPY --from=build /home/gradle/src/build/libs/*.jar /app/TodoBot.jar
# Run application
ENTRYPOINT ["java","-jar","/app/TodoBot.jar"]