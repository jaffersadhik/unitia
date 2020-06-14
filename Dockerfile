# Pull base image
FROM davidcaste/alpine-tomcat:jre8tomcat8
# Maintainer
MAINTAINER "jaffer.sadhik@gmail.com"
# Set Environment properties

RUN rm -rf /opt/tomcat/webapps/ROOT
RUN rm -rf /opt/tomcat/webapps/docs
RUN rm -rf /opt/tomcat/webapps/examples
RUN rm -rf /opt/tomcat/webapps/host-manager
RUN rm -rf /opt/tomcat/webapps/manager



COPY ./*.prop /opt/tomcat/conf/

COPY ./server.xml /opt/tomcat/conf/server.xml

# Copy war file to tomcat webapps folder
COPY ./api/target/api-1.0.war /opt/tomcat/webapps/app.war

EXPOSE 8080

CMD ["/opt/tomcat/bin/catalina.sh", "run"]
VOLUME ["/unitia"]
VOLUME ["/unitialogs"]