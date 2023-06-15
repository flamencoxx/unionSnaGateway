UNION_SERVER_NAME=union-server1
appId=`ps -ef |grep $UNION_SERVER_NAME |grep java|awk '{print $2}'a`
dataStr=$(date +"%Y%m%d%t%T")

echo $dataStr
if [ -z $appId ];
then
    echo "Union server1 not Running"
else
    kill $appId
    echo "stop Union-Server 1"
fi
