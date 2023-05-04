package com.iaspec.uniongatewayserver.controller;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.GatewayInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  17:24
 */
@Controller
public class GatewayController {

    @RequestMapping(value = "/getUnionGatewayInfo", method = RequestMethod.GET)
    public ResponseEntity<GatewayInfo> getUnionGatewayInfo() {
        GatewayInfo info = new GatewayInfo();
        info.setLocalAddress(GatewayConstant.SERVER_LOCAL_HOST);
        info.setLocalPort(GatewayConstant.SERVER_LOCAL_PORT);
        info.setRemoteAddress(GatewayConstant.SERVER_REMOTE_HOST);
        info.setRemotePort(GatewayConstant.SERVER_REMOTE_PORT);
        info.setIpConnectionID(GatewayConstant.IP_CONNECTION_ID.get());
        info.setServerOpenConnectTimes(GatewayConstant.SERVER_OPEN_CONNECT_TIMES.get());
        info.setServerCloseConnectTimes(GatewayConstant.SERVER_CLOSE_CONNECT_TIMES.get());
        info.setAcceptMsgCount(GatewayConstant.ACCEPT_MSG_COUNT.toString());
        info.setSendMsgCount(GatewayConstant.SEND_MSG_COUNT.toString());
        return ResponseEntity.ok(info);
    }

    @RequestMapping(value = "/getAdapterInfo", method = RequestMethod.GET)
    public ResponseEntity<GatewayInfo> getAdapterInfo(){
        boolean running = GatewayConstant.CLIENT_INBOUND_CHANNEL_ADAPTER.isRunning();
        boolean running1 = GatewayConstant.SERVER_OUTBOUND_CHANNEL_ADAPTER.isRunning();
        boolean active = GatewayConstant.CLIENT_INBOUND_CHANNEL_ADAPTER.isActive();
        boolean active1 = GatewayConstant.SERVER_OUTBOUND_CHANNEL_ADAPTER.isActive();
        return ResponseEntity.ok(new GatewayInfo());
    }
}
