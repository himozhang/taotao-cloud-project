#!/bin/bash

function start_zipkin() {
     nohup java -jar /opt/taotao-cloud/zipkin2.23.2/zipkin-server-2.23.2-exec.jar \
        --MYSQL_HOST=172.16.6.151 \
        --MYSQL_TCP_PORT=3306 \
        --MYSQL_DB=taotao-cloud-zipkin \
        --MYSQL_USER=root \
        --MYSQL_PASS=123456 \
     >/opt/taotao-cloud/zipkin2.23.2/start.log 2>&1 &
     sleep 10
     echo " zipkin started"
}

function stop_zipkin() {
     ps -ef | grep zipkin|grep -v grep|awk '{print $2}' |xargs kill -9
     sleep 10
     echo "zipkin stoped"
}

case $1 in
"start")
    start_zipkin
    ;;
"stop")
    stop_zipkin
    ;;
"restart")
    stop_zipkin
    sleep 15
    start_zipkin
    ;;
*)
    echo Invalid Args!
    echo 'Usage: '$(basename $0)' start|stop|restart'
    ;;
esac
