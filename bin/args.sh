#!/bin/sh

## jdk8
# GCLOG="-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${SERVICEPATH}/${SERVICE}/${SERVICE}.gc  "
## jdk11
GCLOG="-Xlog:gc=trace:file=${SERVICEPATH}/${SERVICE}/${SERVICE}.gc:time,pid:filecount=10,filesize=1024000  "
HEAPDUMP="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${SERVICEPATH}/${SERVICE}/${SERVICE}.heap"

ARGS='-Dio.netty.leakDetection.level=ADVANCED'
echo $env
 if [[ $env == 'daily'  ]];then
     ARGS="$ARGS -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=6980"
 fi

#daily
Xms_daily=512m
Xmx_daily=512m

#online
Xms_online=1024m
Xmx_online=1024m