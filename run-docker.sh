#!/bin/bash

docker run -it -v ${PWD}:/notebooks -p 8888:8888 tensorflow/tensorflow:latest-py3

