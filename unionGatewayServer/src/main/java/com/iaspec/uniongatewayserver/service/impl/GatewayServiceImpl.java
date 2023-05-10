package com.iaspec.uniongatewayserver.service.impl;

import com.iaspec.uniongatewayserver.config.UnionTcpNetClientConnectionFactory;
import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.exception.ServiceException;
import com.iaspec.uniongatewayserver.service.GatewayService;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  11:38
 */
public class GatewayServiceImpl implements GatewayService {


    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public void handleServerConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException {
        if (tcpConnectionEvent instanceof TcpConnectionOpenEvent){
            handleUnionServerOpenConnectionEvent(con);
        } else if(tcpConnectionEvent instanceof TcpConnectionCloseEvent){
            handleUnionServerClosedConnectionEvent(con);
        }
    }

    @Override
    public void handleClientConnectionEvent(TcpConnectionEvent tcpConnectionEvent, TcpConnection con) throws ServiceException {
        if (tcpConnectionEvent instanceof TcpConnectionOpenEvent){
            handleUnionClientOpenConnectionEvent(con);
        } else if(tcpConnectionEvent instanceof TcpConnectionCloseEvent){
            handleUnionClientClosedConnectionEvent(con);
        }
    }


    private void handleUnionServerOpenConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionOpenConnectionEvent",true,new String[] {"con"},con);
        try {
            GatewayConstant.SERVER_REMOTE_HOST = con.getHostAddress();
            GatewayConstant.SERVER_REMOTE_PORT = con.getPort();
            GatewayConstant.SERVER_CONNECTION_ID.set(con.getConnectionId());
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            SystemLogger.info("UMPS connect to Union Gateway Server, UMPS : {0} : {1} connect to Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.SERVER_CONNECTION_ID);

        } catch (Exception e) {
            SystemLogger.error("Occurs an error while handle Union Open Connection, error= {0}",new String[]{e.getMessage()},e);
        } finally {
            SystemLogger.debugMethod(getClass(),"handleUnionOpenConnectionEvent",false,new String[] {"con"},con);
        }

    }

    private void handleUnionServerClosedConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionClosedConnectionEvent",true,new String[] {"con"},con);

        try {
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            SystemLogger.info("umps and gateway Server disconnect, UMPS : {0} : {1} disconnect with Gateway: {2} : {3},  connectId : {4}",GatewayConstant.SERVER_REMOTE_HOST,GatewayConstant.SERVER_REMOTE_PORT,localHost,localPort,GatewayConstant.SERVER_CONNECTION_ID);

            GatewayConstant.SERVER_REMOTE_HOST = StringUtils.EMPTY;
            GatewayConstant.SERVER_REMOTE_PORT = 0;
            GatewayConstant.SERVER_CONNECTION_ID.set(StringUtils.EMPTY);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }

    }

    private void handleUnionClientOpenConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionClientOpenConnectionEvent",true,new String[] {"con"},con);
        try {
            GatewayConstant.CLIENT_CONNECTION_ID.set(con.getConnectionId());
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            String remoteHost = con.getSocketInfo()
                    .getInetAddress()
                    .getHostAddress();
            int remoterPort = con.getSocketInfo()
                    .getPort();

            SystemLogger.info("UMPS connect to Union Gateway Client, UMPS : {0} : {1} connect to Gateway: {2} : {3},  connectId : {4}",remoteHost,remoterPort,localHost,localPort,GatewayConstant.CLIENT_CONNECTION_ID);

        } catch (Exception e) {
            SystemLogger.error("Occurs an error while handle Union Open Connection, error= {0}",new String[]{e.getMessage()},e);
        } finally {
            SystemLogger.debugMethod(getClass(),"handleUnionClientOpenConnectionEvent",false,new String[] {"con"},con);
        }

    }

    private void handleUnionClientClosedConnectionEvent(TcpConnection con) {
        SystemLogger.debugMethod(getClass(),"handleUnionClientClosedConnectionEvent",true,new String[] {"con"},con);

        try {
            String localHost = con.getSocketInfo()
                    .getLocalAddress().getHostAddress();
            int localPort = con.getSocketInfo()
                    .getLocalPort();

            String remoteHost = con.getSocketInfo()
                    .getInetAddress()
                    .getHostAddress();
            int remoterPort = con.getSocketInfo()
                    .getPort();

            SystemLogger.info("umps and gateway Client disconnect, UMPS : {0} : {1} disconnect with Gateway: {2} : {3},  connectId : {4}",remoteHost,remoterPort,localHost,localPort,GatewayConstant.CLIENT_CONNECTION_ID);
            GatewayConstant.CLIENT_CONNECTION_ID.set(StringUtils.EMPTY);
            GatewayConstant.CLIENT_FACTORY = applicationContext.getBean(GatewayConstant.CLIENT_FACTORY_NAME, UnionTcpNetClientConnectionFactory.class);
            GatewayConstant.CLIENT_FACTORY.closeConnection(GatewayConstant.CLIENT_CONNECTION_ID.get());
            if(GatewayConstant.CLIENT_FACTORY.getConnection().isOpen()){
                SystemLogger.error("client connect failure,try close again");
                GatewayConstant.CLIENT_FACTORY.getConnection().close();
            }

        } catch (Exception e) {
            SystemLogger.error("Occur a error when handleUnionClientClosedConnectionEvent msg : {0}",e.getMessage());
        } finally {
        }

    }
}
