FROM openjdk:8-jre-alpine3.9
MAINTAINER "jaffer.sadhik@gmail.com"
COPY ./*.prop /
COPY ./hbase*.xml /
COPY ./unitiad/target/unitiad-1.0-jar-with-dependencies.jar /unitiad.jar
EXPOSE 8080

CMD ["java","-cp","/*.xml","-jar", "/unitiad.jar","-Xms=180M","-Xmx=180M"]

VOLUME ["/unitia,/logs"]


