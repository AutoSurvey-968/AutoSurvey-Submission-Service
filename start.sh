#!/bin/bash

#Decode the passed in truststore env variable
base64 --decode $TRUSTSTORE_ENCODED > src/main/resources
java -jar submission-service.jar