FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} Nugger-BE-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "Nugger-BE-0.0.1-SNAPSHOT.jar"]