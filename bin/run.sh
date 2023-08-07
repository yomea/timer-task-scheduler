#!/bin/bash
# Jar Service Startup Scipt
# SHM QKT

USER=admin
SERVICE=$(cd $(dirname $0); pwd | awk -F '/' '{print $(NF-1)}')
MEMORY=1024m
ARGS=''

Start() {
    mem=$1
    if [ -z "$mem" ];then
        mem=$MEMORY
    fi

    proc=$(ps -ef | grep /home/admin/${SERVICE}/lib/${SERVICE}.jar | grep -v grep | wc -l)
    if [[ $proc != 0  ]];then
        exit 5
    fi
    if [[ `whoami` == 'root' ]];then
        exec su admin -c "java -server -Xms${mem} -Xmx${mem}  -jar /home/admin/${SERVICE}/lib/${SERVICE}.jar  $ARGS >> /data/logs/${SERVICE}.log 2>&1 &"
    elif [[ `whoami` == 'admin' ]];then
        exec java -server -Xms${mem} -Xmx${mem}  -jar /home/admin/${SERVICE}/lib/${SERVICE}.jar  $ARGS >> /data/logs/${SERVICE}.log 2>&1 &
    else
        echo "Run with admin user"
        exit 10
    fi
}


Stop() {
    /usr/bin/ps -ef | grep ${SERVICE} | grep -v grep | awk '{print $2}'| xargs kill -9
}

Restart() {
    Stop
    Start
}


case $1 in
    start|run)
        Start $2
        ;;
    stop)
        Stop
        ;;
    restart)
        Restart
        ;;
esac
