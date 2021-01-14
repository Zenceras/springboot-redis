# REDIS REST Application

REDIS REST Application is a simple Spring Boot application that communicates
with the local REDIS server.

## Download

Download the [latest release][] from GitHub.

  [latest release]: https://github.com/Zenceras/springboot-redis

## Install

Install Java and Maven - you can use [SDKMAN][] for this.

  [SDKMAN]: https://sdkman.io/ 
  
Install [Docker][] and [Docker Compose][] to run the application in Docker 
containers.

  [Docker]: https://docs.docker.com/ 
  [Docker Compose]: https://docs.docker.com/compose/  
  
Open a terminal in the project folder and run this Maven command to build 
project:

```
mvn clean install
```

Run by this Docker Compose command 2 containers with Spring Boot Application
and REDIS: 

```
docker-compose up
```

## Use

### Publish a new message

To publish a new message on the Server you can use this Curl:

```
curl --request POST \
  --url http://localhost:8080/publish \
  --header 'Accept:  application/json' \
  --header 'Content-Type: application/json' \
  --data '{
	"content": "Message1"
}'
```

This is message payload(can be any String):

```
"content": "Message1"

```

A response will contain the Timestamp of this message in the REDIS:

```
2021-01-14T07:27:28.655Z

```

### Retrieve the last message

To retrieve the last message from the Server you can use this Curl:
                                              
```
curl --request GET \
  --url http://localhost:8080/getLast \
  --header 'Accept: application/json'

```

A response will contain the last message from the REDIS:

```
Message1

```

### Retrieve all messages between two timestamps

To retrieve all messages that were on the REDIS and occurred 
between two given timestamps you can use this Curl and send two
parameters:
1. ```start``` - start timestamp to retrieving messages
2. ```end``` - end timestamp to retrieving messages

```
curl --request GET \
  --url 'http://localhost:8080/getByTime?start=2021-01-14T06%3A33%3A56.316Z&end=2021-01-14T06%3A33%3A58.282Z' \
  --header 'Accept: application/json'

```

A response will contain all messages between these two 
timestamps from the REDIS:

```
[
  "Message2",
  "Message3"
]

```
