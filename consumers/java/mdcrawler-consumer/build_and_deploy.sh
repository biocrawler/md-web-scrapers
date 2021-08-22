#!/bin/bash

IMAGE=fosshost.perpetualnetworks.org:5000/mdcrawler-consumer:latest

#packaging app
mvn clean package

package=$?

if [[ $package == 0 ]]; then
#building image
docker build . --tag $IMAGE

#pushing image to repository
docker push $IMAGE
else
echo "must fix package erros before deploying"
fi


