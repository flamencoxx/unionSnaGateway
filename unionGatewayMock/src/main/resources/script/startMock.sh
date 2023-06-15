BASE_DIR=$(dirname $(realpath $0))
nohup java -jar -DlogDir=$BASE_DIR/log $BASE_DIR/unionGatewayMock-1.jar &
