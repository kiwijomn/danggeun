#!/usr/bin/env bash

REPOSITORY=/home/ec2-user

cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/  | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME
CURRENT_PID=$(pgrep -fl $JAR_PATH | awk '{print $1}')

if [ -z $CURRENT_PID ]
then
  echo "> Nothing to end."
else
  echo "> kill $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH Run"

nohup java -jar \
  -Dspring.config.location=file:///$REPOSITORY/application.yml \
  $JAR_PATH >> /$REPOSITORY/log.out 2>&1 &
