module=$1
exception=$2
dateStr=$(date +"%Y%m%d%t%T")
SMTP_SERVER="smtp.gmail.com"
SENDER="1161514079fzc@gmail.com"
RECIPIENT="zicong.feng@iaspec.com"
SUBJECT="Union GateWay Notification – $module"

MESSAGE=$(printf "Dear Esteemed Users,\nYou have an incoming notification, please refer to below.\n\nDate & Time:$dateStr\nModule: $module\nReason: $exception\n\nThank you\n(Note: This email is generated and sent out automatically)")

RETVAL=0
echo "$MESSAGE" | mailx -v -r "$SENDER" -S smtp="$SMTP_SERVER" -s "$SUBJECT" $RECIPIENT
echo $MESSAGE >>/opt/iaspec/union-gateway/logs/union_gateway_mail.log
echo $2 | base64 -d
RETVAL=$?

exit $RETVAL
echo "$MESSAGE" >> /opt/iaspec/union-gateway/logs/union_gateway_exception_alert.err.log
exit 0

