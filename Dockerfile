# 빌드 스테이지
FROM gradle:4.10.2-jdk8-alpine as build

ENV APP_HOME=/apps

WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN chmod +x gradlew
RUN ./gradlew clean build

COPY src $APP_HOME/src


# 런타임 스테이지
FROM openjdk:8-jdk

ENV APP_HOME=/apps
ARG ARTIFACT_NAME=app.jar
ARG JAR_FILE_PATH=build/libs/*.jar

WORKDIR $APP_HOME

COPY --from=build $APP_HOME/$JAR_FILE_PATH $ARTIFACT_NAME

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
