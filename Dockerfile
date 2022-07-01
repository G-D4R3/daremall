FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/daremall-0.0.1-SNAPSHOT.jar
VOLUME /tmp
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]