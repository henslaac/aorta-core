FROM openjdk:10
RUN apt-get -y update && apt-get -y install tesseract-ocr
RUN export LC_ALL=C
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080
COPY target/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar