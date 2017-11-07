#!/bin/bash

IMAGE_NAME="keras-jupyter"

docker build -t ${IMAGE_NAME} .

docker run --rm -v ${PWD}/notebooks:/notebooks -p 8888:8888 ${IMAGE_NAME}
