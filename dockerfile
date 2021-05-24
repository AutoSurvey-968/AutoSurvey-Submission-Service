FROM openjdk:8-jdk-alpine
WORKDIR /app

COPY target/submission-service.jar .
COPY src/main/resources/cassandra_truststore.jks src/main/resources/
EXPOSE 8081

CMD [ "java", "-jar", "submission-service.jar" ]