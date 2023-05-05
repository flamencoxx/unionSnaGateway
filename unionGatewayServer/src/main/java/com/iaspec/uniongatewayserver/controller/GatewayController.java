package com.iaspec.uniongatewayserver.controller;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.GatewayInfo;
import com.iaspec.uniongatewayserver.util.CpicUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.charset.StandardCharsets;

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
        return ResponseEntity.ok(new GatewayInfo());
    }

    @RequestMapping(value = "/serverAdapterSend" ,method = RequestMethod.POST)
    public void send2UMPSTest(){
        String str = "flamencoTest";
        byte[] data = CpicUtil.convertToEbc(str.getBytes(StandardCharsets.US_ASCII), str.getBytes(StandardCharsets.US_ASCII).length);
        Message<byte[]> message = MessageBuilder.withPayload(data)
                .setHeader(IpHeaders.CONNECTION_ID, GatewayConstant.IP_CONNECTION_ID.get())
                .build();
    }

    @RequestMapping(value = "/unionClient2Mock" ,method = RequestMethod.POST)
    public void send2Mock(){
        String str = "flamencoTest";
        byte[] data = CpicUtil.convertToEbc(str.getBytes(StandardCharsets.US_ASCII), str.getBytes(StandardCharsets.US_ASCII).length);
        Message<byte[]> message = MessageBuilder.withPayload(data)
                .setHeader(IpHeaders.CONNECTION_ID, GatewayConstant.IP_CONNECTION_ID.get())
                .build();
        GatewayConstant.CLIENT_OUTBOUND_CHANNEL.send(message);
    }

}
