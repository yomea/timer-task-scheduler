#!/bin/sh
source /etc/profile

if [ -z "$2" ]; then
        echo $1 with online
        env=online
        doWhat=$1
else
        env=$1
        doWhat=$2
fi

USER=admin
SERVICEPATH=/home/admin
SERVICE=$(cd $(dirname $0); pwd | awk -F '/' '{print $(NF-1)}')

source $SERVICEPATH/${SERVICE}/bin/args.sh

# SERVICE=可以手动定义，默认取上一级目录名
LOGS=/data/logs
mkdir -pv ${LOGS}/${SERVICE}

eval Xms="\$Xms_$env"
eval Xmx="\$Xmx_$env"
echo $Xms
echo $Xmx
Start() {
    proc=$(ps -ef | grep ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar | grep -v grep | wc -l)
    if [[ $proc != 0  ]];then
        exit 5
    fi
    if [[ `whoami` == 'root' ]];then
        exec su admin -c "java -server -Xms$Xms -Xmx$Xmx ${GCLOG}  ${HEAPDUMP} $ARGS -Dlogging.config=${SERVICEPATH}/${SERVICE}/config/logback-spring.xml -Dspring.config.location=${SERVICEPATH}/${SERVICE}/config/application.yml   -jar ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar >> ${SERVICEPATH}/${SERVICE}/${SERVICE}.log 2>&1 &"
    elif [[ `whoami` == 'admin' ]];then
        exec java -server -Xms$Xms -Xmx$Xmx ${GCLOG}  ${HEAPDUMP}  $ARGS -Dlogging.config=${SERVICEPATH}/${SERVICE}/config/logback-spring.xml -Dspring.config.location=${SERVICEPATH}/${SERVICE}/config/application.yml   -jar ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar  >> ${SERVICEPATH}/${SERVICE}/${SERVICE}.log 2>&1 &
    else
        echo "Run with admin user"
        exit 10
    fi
}

Stop() {
    ps -ef | grep ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar | grep -v grep | awk '{print $2}'| xargs kill
    sleep 3
    proc=$(ps -ef | grep ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar | grep -v grep | wc -l)
    if [[ $proc != 0 ]];then
        ps -ef | grep ${SERVICEPATH}/${SERVICE}/${SERVICE}.jar | grep -v grep | awk '{print $2}'| xargs kill -9
    fi
}

Restart() {
    Stop
    Start
}

case $doWhat in
    start|run)
        Start
        ;;
    stop)
        Stop
        ;;
    restart)
        Restart
        ;;
    *)
        Start
        ;;
esac
