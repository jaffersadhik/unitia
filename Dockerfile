# Pull base image
FROM davidcaste/alpine-tomcat:jre8tomcat8
# Maintainer
MAINTAINER "jaffer.sadhik@gmail.com"
# Set Environment properties

RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/docs
RUN rm -rf /usr/local/tomcat/webapps/examples
RUN rm -rf /usr/local/tomcat/webapps/host-manager
RUN rm -rf /usr/local/tomcat/webapps/manager

COPY numeringplan/*.prop /usr/local/tomcat/conf/

COPY server.xml /usr/local/tomcat/conf/server.xml

# Copy war file to tomcat webapps folder
COPY api/target/*.war /usr/local/tomcat/webapps/app.war

