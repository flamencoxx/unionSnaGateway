package com.iaspec.uniongatewayserver.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.integration.ip.tcp.connection.TcpSocketFactorySupport;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

/**
 * @author Flamenco.xxx
 * @date 2023/4/26  14:51
 */
@Configurable
public class UnionTcpNetSocketFactorySupport implements TcpSocketFactorySupport {

    private static UnionGatewaySocketFactory theFactory;

    @Override
    public ServerSocketFactory getServerSocketFactory() {
        return ServerSocketFactory.getDefault();
    }

    @Override
    public SocketFactory getSocketFactory() {
        synchronized (SocketFactory.class) {
            if (theFactory == null) {
                theFactory = new UnionGatewaySocketFactory();
            }
        }

        return theFactory;
    }
}
