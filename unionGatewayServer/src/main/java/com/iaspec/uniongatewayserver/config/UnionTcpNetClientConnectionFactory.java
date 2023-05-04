package com.iaspec.uniongatewayserver.config;

import org.springframework.integration.ip.tcp.connection.DefaultTcpNetConnectionSupport;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetConnectionSupport;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Flamenco.xxx
 * @date 2023/4/17  16:59
 */
public class UnionTcpNetClientConnectionFactory extends TcpNetClientConnectionFactory {
    /**
     * Creates a TcpNetClientConnectionFactory for connections to the host and port.
     *
     * @param host the host
     * @param port the port
     */
    private TcpNetConnectionSupport tcpNetConnectionSupport = new DefaultTcpNetConnectionSupport();

    public UnionTcpNetClientConnectionFactory(String host, int port) {
        super(host, port);
    }

    @Override
    protected Socket createSocket(String host, int port) throws IOException {
        return new Socket();
    }

}
