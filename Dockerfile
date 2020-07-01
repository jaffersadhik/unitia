FROM openjdk:8-jre-alpine3.9
MAINTAINER "jaffer.sadhik@gmail.com"
COPY ./*.prop /
COPY ./unitiadngen/target/unitiadngen-1.0-jar-with-dependencies.jar /app.jar
EXPOSE 8080


VOLUME ["/unitia"]

CMD ["java", "-jar", "/app.jar"]
