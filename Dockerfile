FROM gradle:8.10.0-jdk17-jammy AS build_image

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY . .
RUN gradle clean build -x spotlessCheck

FROM amazoncorretto:17

ENV JAR_FILE=VSTechTest-1.0.jar

WORKDIR /opt/app
COPY --from=build_image /usr/app/build/libs/$JAR_FILE .
EXPOSE 8000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar $JAR_FILE"]
