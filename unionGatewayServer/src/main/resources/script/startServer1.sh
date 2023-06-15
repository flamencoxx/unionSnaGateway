BASE_DIR=$(dirname $(realpath $0))
dateStr=$(date +"%Y-%m-%d-%t-%T")
nohup java -jar -Dlog.dir=/opt/iaspec/union-gateway/logs/union-server1 -Dspring.config.location=$BASE_DIR/config/application.properties $BASE_DIR/unionSnaGateway-1.0.jar &

echo dateStr "union server 1 start"
