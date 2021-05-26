FROM openjdk:8-jdk-alpine
WORKDIR /app

COPY target/submission-service.jar .
COPY start.sh .
EXPOSE 8080

CMD [ "./start.sh" ]