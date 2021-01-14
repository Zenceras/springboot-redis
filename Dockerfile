FROM openjdk:8-jre-alpine
COPY ./target/redis-rest-0.0.1-SNAPSHOT.jar  /usr/src/app/redis-rest.jar
WORKDIR /usr/src/app
EXPOSE 8080
CMD java -jar /usr/src/app/redis-rest.jar
