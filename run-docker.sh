#!/bin/bash

IMAGE_NAME="keras-jupyter"

docker build -t ${IMAGE_NAME} .

CONTAINER_ID=$(docker run -d -it --rm -v ${PWD}/notebooks:/notebooks -p 6006:6006 -p 8888:8888 ${IMAGE_NAME})I

docker logs ${CONTAINER_ID}
