BASE_DIR=$(dirname $(realpath $0))
SERVER_NAME=$1
EXCEPTION_MSG=$2
SEND_MAIL="$BASE_DIR/sendEmail.sh"
LOG_FILE="$BASE_DIR/logs/abnormal_shutdown.log"
echo $BASE_DIR >> $LOG_FILE

echo "server name : $SERVER_NAME , msg : $EXCEPTION_MSG"

$SEND_MAIL $SERVER_NAME "$EXCEPTION_MSG \n system abnormal shutdown\n"
