package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.integration.ip.tcp.connection.TcpListener;
import org.springframework.messaging.Message;

/**
 * @author Flamenco.xxx
 * @date 2023/5/9  14:13
 */
public class ClientIdleTcpListener implements TcpListener {
    @Override
    public boolean onMessage(Message<?> message) {
        byte[] data = (byte[]) message.getPayload();
        if (data.length == 0){
            SystemLogger.info("Accept a idle Message");
            return false;
        }
        return true;
    }
}
