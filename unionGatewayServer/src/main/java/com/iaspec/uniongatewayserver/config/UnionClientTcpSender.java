package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpSender;

/**
 * @author Flamenco.xxx
 * @date 2023/4/17  16:34
 */
public class UnionClientTcpSender implements TcpSender {
    @Override
    public void addNewConnection(TcpConnection con) {
        GatewayConstant.isClientConnect = true;
        GatewayConstant.CLIENT_OPEN_CONNECT_TIMES.getAndIncrement();
        GatewayConstant.CLIENT_CONNECTION_ID.set(con.getConnectionId());
        UnionTcpNetClientConnectionSupport.retryCount.set(0);
        SystemLogger.info("UMPS  connect to Union Gateway Client, UMPS : {0} : {1} connect to Gateway: {2} : {3},  connectId : {4}",
                con.getSocketInfo().getInetAddress().getHostAddress(),con.getSocketInfo().getPort(),
                con.getSocketInfo().getLocalAddress(),
                con.getSocketInfo().getLocalPort(),
                GatewayConstant.SERVER_CONNECTION_ID);
    }

    @Override
    public void removeDeadConnection(TcpConnection con) {
        try {
            TcpSender.super.removeDeadConnection(con);
            GatewayConstant.isClientConnect = false;
            GatewayConstant.CLIENT_CLOSE_CONNECT_TIMES.getAndIncrement();
            GatewayConstant.CLIENT_CONNECTION_ID.set(StringUtils.EMPTY);
            UnionTcpNetClientConnectionSupport.retryCount.set(0);
            SystemLogger.info("UMPS and gateway Client disconnect, UMPS : {0} : {1} disconnect with Gateway: {2} : {3},  connectId : {4}",
                    con.getSocketInfo().getInetAddress().getHostAddress(),
                    con.getSocketInfo().getPort(),
                    con.getSocketInfo().getLocalAddress(),
                    con.getSocketInfo().getLocalPort(),
                    GatewayConstant.SERVER_CONNECTION_ID);
        } catch (Throwable e) {
            SystemLogger.error("Occur a error when client remove dead connection, error message: {0}",e.getMessage());
        }
    }
}
