#!/bin/bash

echo "-- starting minishift, if not already started"

minishift start

echo "-- setting oc env"
eval $(minishift oc-env)

echo "-- setting docker env"
eval $(minishift docker-env)

echo "-- logging into registry"
docker login -u developer -p $(oc whoami -t) $(minishift openshift registry)

echo "-- building docker image"
docker build -t name:jenkins .

echo "-- Tag image"
docker tag name:jenkins $(minishift openshift registry)/test/jenkins

echo "-- Push image"
docker push $(minishift openshift registry)/test/jenkins
