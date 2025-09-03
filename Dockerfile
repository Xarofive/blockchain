FROM eclipse-temurin:21-jdk as builder

WORKDIR /usr/src/app

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew build --no-daemon

FROM eclipse-temurin:21-jre

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]