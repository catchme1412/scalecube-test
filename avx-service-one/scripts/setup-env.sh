#!/usr/bin/env bash

declare -a addresses=("ip1" "ip2")

CERT_PATH=
SRC_PATH=
USER_NAME=

cd $SRC_PATH

mvn clean install -DskipTests

for addr in ${addresses[@]}
do
    echo "####### Setting up for: #######"
    echo "$addr"
    ssh -oStrictHostKeyChecking=no -i $CERT_PATH $USER_NAME@$addr 'sudo rm -rf /tmp/*'
    ssh -oStrictHostKeyChecking=no -i $CERT_PATH $USER_NAME@$addr 'mkdir -p /tmp/rajesh/scripts/'
    ssh -oStrictHostKeyChecking=no -i $CERT_PATH $USER_NAME@$addr 'mkdir -p /tmp/rajesh/target/lib'
    scp -r -i $CERT_PATH $SRC_PATH/avx-service-one/target/*.jar $USER_NAME@$addr:/tmp/rajesh/target/
    scp -r -i $CERT_PATH $SRC_PATH/avx-service-one/target/classes/*.properties $USER_NAME@$addr:/tmp/rajesh/target/lib/
    scp -r -i $CERT_PATH $SRC_PATH/avx-service-one/target/lib/* $USER_NAME@$addr:/tmp/rajesh/target/lib/
    scp -r -i $CERT_PATH $SRC_PATH/avx-service-one/scripts/* $USER_NAME@$addr:/tmp/rajesh/scripts/
done
