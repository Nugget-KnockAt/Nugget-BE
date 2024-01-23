FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/Nugget-BE-0.0.1-SNAPSHOT.jar
COPY build/libs/Nugget-BE-0.0.1-SNAPSHOT.jar Nugget-BE-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "Nugget-BE-0.0.1-SNAPSHOT.jar"]