#!/usr/bin/env bash

cd $(dirname $0)
cd ../
echo `pwd`

JAR_FILE=$(ls target |grep jar)

java \
-Dio.scalecube.services.examples.seeds=localhost:4802 \
-Dio.scalecube.services.examples.memberHost=localhost \
-cp target/${JAR_FILE}:target/lib/* ${JVM_OPTS} com.appviewx.gossip.ConsumerService
