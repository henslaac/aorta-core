FROM maven:latest AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:10
RUN apt-get -y update && apt-get -y install tesseract-ocr
RUN export LC_ALL=C
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080
COPY --from=build /usr/src/app/target/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar