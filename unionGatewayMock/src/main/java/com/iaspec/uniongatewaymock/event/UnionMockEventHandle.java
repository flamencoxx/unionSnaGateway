package com.iaspec.uniongatewaymock.event;

import com.iaspec.uniongatewaymock.service.GatewayService;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionFailedEvent;
import org.springframework.messaging.Message;

import javax.annotation.Resource;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  14:41
 */
public class UnionMockEventHandle {

    @Resource
    private GatewayService gatewayService;

    public void handleEvent(Message<TcpConnectionEvent> message){
        SystemLogger.debugMethod(getClass(),"handleEvent",
                true,new  String[]{"message"},message.getHeaders());

        try {
            TcpConnectionEvent tcpConnectionEvent = message.getPayload();
            if (tcpConnectionEvent instanceof TcpConnectionExceptionEvent){
                TcpConnectionExceptionEvent tcpConnectionExceptionEvent = (TcpConnectionExceptionEvent) tcpConnectionEvent;
                SystemLogger.error("testHandleExceptionEvent msg is {0}",new String[]{tcpConnectionExceptionEvent.getSource().getClass().getName()},new Throwable());
            }


            TcpConnection con = ((TcpConnection) tcpConnectionEvent.getSource());
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            gatewayService.handleMockClientConnectionEvent(tcpConnectionEvent,con);


        } catch (Exception e) {
            SystemLogger.error("Occurs error while handleClientEvent, e.message = {0}",new String[]{e.getMessage()},e);
        } finally {
            SystemLogger.debugMethod(getClass(),"handleEvent",
                    false,new  String[]{"message"},message.getHeaders());
        }
    }
}
