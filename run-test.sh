#!/bin/bash

DOCKERHUB_USERNAME="juliasmagina"
IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api}
FULL_IMAGE_NAME="$DOCKERHUB_USERNAME/$IMAGE_NAME"
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP
TAG="latest"

echo ">>>> Building tests"
docker build -t $IMAGE_NAME .

echo ">>>> Tagging image for Docker Hub"
docker tag $IMAGE_NAME $FULL_IMAGE_NAME:$TAG

echo ">>>> Pushing image to Docker Hub"
docker push $FULL_IMAGE_NAME:$TAG

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"


echo ">>>>Tests running"
docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE"\
  -e APIBASEURL=http://localhost:4111 \
  -e UIBASEURL=http://localhost:4111 \
$IMAGE_NAME


echo "Test ended"
echo "Log file: $TEST_OUTPUT_DIR/logs/run.log"
echo "Result: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"

