package com.iaspec.uniongatewayserver.event;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.service.GatewayService;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.messaging.Message;

import javax.annotation.Resource;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  11:27
 */
public class UnionEventHandler {

    @Resource
    private GatewayService gatewayService;

    public void handleEvent(Message<TcpConnectionEvent> message){
        SystemLogger.debugMethod(getClass(),"handleEvent",
                true,new  String[]{"message"},message.getHeaders());

        try {
            TcpConnectionEvent tcpConnectionEvent = message.getPayload();
//            if(tcpConnectionEvent instanceof TcpConnectionExceptionEvent){
//                TcpConnectionExceptionEvent tcpConnectionExceptionEvent = (TcpConnectionExceptionEvent) tcpConnectionEvent;
//                SystemLogger.error("into tcpConnectException : type is {0}",tcpConnectionExceptionEvent.getCause().getClass().toString());
//                SystemLogger.error("exception info : {0}", tcpConnectionEvent.getCause().getMessage());
//                if (StringUtils.equals(tcpConnectionExceptionEvent.getConnectionFactoryName(), GatewayConstant.SERVER_FACTORY_NAME)) {
//                    SystemLogger.error("ServerFactory connect miss exception, exception type : {0} ,errorMsg : {1}, error source : {2}",new String[]{tcpConnectionExceptionEvent.getCause().getClass().getName(),tcpConnectionExceptionEvent.getCause().getMessage(),tcpConnectionExceptionEvent.getSource().toString()},tcpConnectionExceptionEvent.getCause());
//                }else {
//                    SystemLogger.error("ClientFactory connect miss exception");
//                }
//            }

            TcpConnection con = ((TcpConnection) tcpConnectionEvent.getSource());
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            if (con.getSocketInfo().getLocalPort() ==  GatewayConstant.SERVER_LOCAL_PORT) {
                gatewayService.handleServerConnectionEvent(tcpConnectionEvent,con);
            }else {
                gatewayService.handleClientConnectionEvent(tcpConnectionEvent,con);
            }




        } catch (Exception e) {
            SystemLogger.error("Occurs error while handleClientEvent, e.message = {0}",new String[]{e.getMessage()},e);
        } finally {
            SystemLogger.debugMethod(getClass(),"handleEvent",
                    false,new  String[]{"message"},message.getHeaders());
        }
    }
}
