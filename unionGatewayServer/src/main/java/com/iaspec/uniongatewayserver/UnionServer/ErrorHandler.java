package com.iaspec.uniongatewayserver.UnionServer;



import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;

/**
 * @author Flamenco.xxx
 * @date 2022/12/14  16:37
 */
public class ErrorHandler {


    public void errorHandle(Message<MessageHandlingException> message) {
        SystemLogger.error("Handle a inbound error Msg, msgContent = {0}",message.getPayload()
                .getFailedMessage());
        SystemLogger.error("error Msg, msg = {0}",message.getPayload().getMessage(),new Throwable(message.getPayload().getCause()));
        SystemLogger.info("error Type : {0}",message.getPayload().getCause().getClass().getName());
    }
}
