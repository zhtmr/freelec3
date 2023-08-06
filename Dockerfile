# openjdk8 기반에서 구동
FROM openjdk:8-jdk

# 플랫폼 확인
ARG BUILDPLATFORM

RUN echo $BUILDPLATFORM

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]