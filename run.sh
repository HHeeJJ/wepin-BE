#!/bin/bash

REPOSITORY=/home/ubuntu/myHEJProject/wepin-api
cd $REPOSITORY

nohup java -jar $REPOSITORY/build/libs/wepinAPI-1.0-SNAPSHOT.jar
