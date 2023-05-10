package com.iaspec.uniongatewayserver.service;

import com.iaspec.uniongatewayserver.exception.ServiceException;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  11:38
 */
public interface GatewayService {


    void handleServerConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException;

    void handleClientConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException;

}
