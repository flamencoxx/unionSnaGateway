package com.iaspec.uniongatewaymock.service;

import com.iaspec.uniongatewaymock.excepetion.ServiceException;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  14:43
 */
public interface GatewayService {

    void handleMockClientConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException;

}
