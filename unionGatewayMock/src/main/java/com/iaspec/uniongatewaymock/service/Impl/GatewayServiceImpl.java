package com.iaspec.uniongatewaymock.service.Impl;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.excepetion.ServiceException;
import com.iaspec.uniongatewaymock.service.GatewayService;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Service;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  14:44
 */
@Service
public class GatewayServiceImpl implements GatewayService {

    @Override
    public void handleMockClientConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException {
        if (tcpConnectionEvent instanceof TcpConnectionOpenEvent){
            handleUnionOpenConnectionEvent(con);
        } else if(tcpConnectionEvent instanceof TcpConnectionCloseEvent){
            handleUnionClosedConnectionEvent(con);
        }
    }

    private void handleUnionOpenConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionOpenConnectionEvent",true,new String[] {"con"},con);
        try {
            GatewayConstant.SERVER_REMOTE_HOST = con.getHostAddress();
            GatewayConstant.SERVER_REMOTE_PORT = con.getPort();
            GatewayConstant.IP_CONNECTION_ID.set(con.getConnectionId());
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            SystemLogger.info("UMPS connect to Union Gateway, UMPS : {0} : {1} connect to Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.IP_CONNECTION_ID);

        } catch (Exception e) {
            SystemLogger.error("Occurs an error while handle Union Open Connection, error= {0}",new String[]{e.getMessage()},e);
        } finally {
            SystemLogger.debugMethod(getClass(),"handleUnionOpenConnectionEvent",false,new String[] {"con"},con);
        }

    }

    private void handleUnionClosedConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionClosedConnectionEvent",true,new String[] {"con"},con);

        try {
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            SystemLogger.info("umps and gateway disconnect, UMPS : {0} : {1} disconnect with Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.IP_CONNECTION_ID);

            GatewayConstant.SERVER_REMOTE_HOST = StringUtils.EMPTY;
            GatewayConstant.SERVER_REMOTE_PORT = 0;
            GatewayConstant.IP_CONNECTION_ID.set(StringUtils.EMPTY);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }

    }
}
