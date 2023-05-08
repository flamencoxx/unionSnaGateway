package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpSender;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  16:53
 */
public class UnionServerTcpSender implements TcpSender {
    @Override
    public void addNewConnection(TcpConnection con) {
        try {
            GatewayConstant.isServerConnect = true;
            GatewayConstant.SERVER_REMOTE_HOST = con.getHostAddress();
            GatewayConstant.SERVER_REMOTE_PORT = con.getPort();
            GatewayConstant.SERVER_CONNECTION_ID.set(con.getConnectionId());
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();
            SystemLogger.info("UMPS  connect to Union Gateway Server, UMPS : {0} : {1} connect to Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.SERVER_CONNECTION_ID);
            GatewayConstant.SERVER_OPEN_CONNECT_TIMES.getAndIncrement();
        } catch (Exception e) {
            SystemLogger.error("Occurs an error while handle Union Open Connection, error= {0}",new String[]{e.getMessage()},e);
        }

    }

    @Override
    public void removeDeadConnection(TcpConnection con) {

        try {
            GatewayConstant.isServerConnect = false;
            TcpSender.super.removeDeadConnection(con);
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();
            SystemLogger.info("UMPS and gateway Server disconnect, UMPS : {0} : {1} disconnect with Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.SERVER_CONNECTION_ID);

            GatewayConstant.SERVER_REMOTE_HOST = StringUtils.EMPTY;
            GatewayConstant.SERVER_REMOTE_PORT = 0;
            GatewayConstant.SERVER_CONNECTION_ID.set(StringUtils.EMPTY);
            GatewayConstant.SERVER_CLOSE_CONNECT_TIMES.getAndIncrement();
        } catch (Exception e) {
            SystemLogger.error("Occurs an error while handle Union close Connection, error= {0}",new String[]{e.getMessage()},e);
        }
    }
}
