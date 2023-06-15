BASE_DIR=$(dirname $(realpath $0))
SERVER_NAME=$1
LOG_FILE="$BASE_DIR/logs/normal_shutdown.log"
SEND_MAIL="$BASE_DIR/sendEmail.sh"
echo $(date "+%Y-%m-%d %T") >> $LOG_FILE

if [ -z $SERVER_NAME ];
then
    echo "server name is empty,stop send email" >> $LOG_FILE
else
    $SEND_MAIL $SERVER_NAME "System has been normal shutdown"
fi
