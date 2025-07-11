# syntax=docker/dockerfile:1
FROM gradle:7.6.1-jdk17-alpine AS builder
WORKDIR /app
COPY build.gradle.kts .
COPY gradle.properties .
COPY ./library ./library
COPY settings.gradle.kts.build settings.gradle.kts
RUN echo 'include("apps:notification-service")' >> settings.gradle.kts
COPY ./apps/notification-service ./apps/notification-service
RUN --mount=type=cache,id=gradle,target=/root/.gradle \
    --mount=type=cache,id=gradle,target=/home/gradle/.gradle \
    gradle :apps:notification-service:bootJar --no-daemon

FROM openjdk:17-alpine
EXPOSE 8400
WORKDIR /app
COPY --from=builder /app/apps/notification-service/build/libs/*.jar notification-service.jar
ENTRYPOINT ["java", "-jar" ,"notification-service.jar"]
