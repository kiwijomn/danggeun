#!/usr/bin/env bash

REPOSITORY=/home/ec2-user
PROJECT=danggeun

cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/$PROJECT/  | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$PROJECT/$JAR_NAME
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
  -Dspring.config.location=file:///$REPOSITORY/$PROJECT/application.yml \
  $JAR_PATH >> /$REPOSITORY/$PROJECT/log.out 2>&1 &