package com.iaspec.uniongatewaymock.config;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpSender;

/**
 * @author Flamenco.xxx
 * @date 2023/4/26  15:41
 */
public class UnionMockServerSender implements TcpSender {
    @Override
    public void addNewConnection(TcpConnection con) {
        GatewayConstant.mockServerConnectionId = con.getConnectionId();
        SystemLogger.info("server connect open msg = {0}",con.getSocketInfo().toString());
    }

    @Override
    public void removeDeadConnection(TcpConnection con) {
        TcpSender.super.removeDeadConnection(con);
        SystemLogger.info("server connect close msg = {0}",con.getSocketInfo().toString());
        GatewayConstant.mockServerConnectionId = "";
    }
}
