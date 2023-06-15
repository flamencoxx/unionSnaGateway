mockName='unionGatewayMock'

appId=`ps -ef |grep $mockName |grep java|awk '{print $2}'`

if [ -z $appId ];
then
    echo "mock not running"
else
    kill $appId
    echo "stop mock"
fi
