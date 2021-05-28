#!/bin/sh

# Check if autosurvey-network network exists
if [ -z "$(docker network ls -q -f name=autosurvey-network)" ]; then
    docker network create autosurvey-network
fi

# rm submission-serivce container if it exists
if [ -n "$(docker container ls -aqf name=submission-service)" ]; then
    echo "Removing submission-service"
    docker container stop submission-service
    docker container rm submission-service
fi

#start submission-service container
docker container run -d --name submission-serivce --network autosurvey-network -e EUREKA_URL -e CREDENTIALS_JSON -e CREDENTIALS_JSON_ENCODED -e FIREBASE_API_KEY -e SERVICE_ACCOUNT_ID -e AWS_PASS -e AWS_submission -e TRUSTSTORE_PASS -e TRUSTSTORE_ENCODED autosurvey/submission-service