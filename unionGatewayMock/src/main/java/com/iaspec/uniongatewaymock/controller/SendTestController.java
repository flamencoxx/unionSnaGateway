package com.iaspec.uniongatewaymock.controller;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSONObject;


import com.iaspec.uniongatewaymock.config.RecordJob;
import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.model.TimeNewCons;
import com.iaspec.uniongatewaymock.util.CpicUtil;
import com.iaspec.uniongatewaymock.util.FlamencoUtil;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionSupport;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author Flamenco.xxx
 * @date 2022/8/19  15:52
 */
@Controller
public class SendTestController {

    @Autowired
    private ApplicationContext applicationContext;


    private static AbstractClientConnectionFactory client;

    private static MessageChannel unionServerOutboundChannel;


    @PostConstruct
    void init() {
        unionServerOutboundChannel = applicationContext.getBean("unionOutboundChannel",MessageChannel.class);

        client = applicationContext.getBean("unionMockClientFactory", AbstractClientConnectionFactory.class);
        
    }

    @RequestMapping(value = "/mockClient2GatewayServer", method = RequestMethod.POST)
    public ResponseEntity<Boolean> sendMsg(@RequestParam String content) {
        boolean bool = false;
        try {
            GatewayConstant.sendRecords.put(FlamencoUtil.convert2Accept(content),new TimeInterval());
            handleSendOnecMsg(applicationContext, client, content);
            SystemLogger.info("send Msg to gateway, msg= {0}",content);
            bool = true;

        } catch (Exception e) {
            SystemLogger.error("Error while sending message, e.message= {0}",new String[]{e.getMessage()}, e);
        } finally {
//            SystemLogger.info("SendMsg success, method=mockClient2GatewayServer");
        }

        return new ResponseEntity<>(bool, HttpStatus.OK);
    }

    @RequestMapping(value = "/mockServer2GatewayClient", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> sendMsg2Client(@RequestParam String content){
//        MessageChannel channel = applicationContext.getBean("jetcoOutboundChannel",MessageChannel.class);
        byte[] bytes = content.getBytes(StandardCharsets.US_ASCII);
        byte[] res;
        if(GatewayConstant.IS_EBC_OR_ASCII){
            res = CpicUtil.convertToEbc(bytes, bytes.length);
        }else{
            res = bytes;
        }
        byte[] lenBytes = converContent(res).getBytes(StandardCharsets.US_ASCII);
        byte[] total = new byte[res.length + lenBytes.length];
        System.arraycopy(lenBytes, 0, total, 0, 4);
        System.arraycopy(res, 0, total, 4, res.length);
        GatewayConstant.sendRecords.put(FlamencoUtil.convert2Accept(content),new TimeInterval());
        String connectionId = GatewayConstant.CONNECTION_ID;
        Message<byte[]> message = MessageBuilder.withPayload(total)
                .setHeader(IpHeaders.CONNECTION_ID, connectionId)
                .build();
        boolean isSend = unionServerOutboundChannel.send(message);
        SystemLogger.info("send Msg to gateway, msg= {0}",content);
        if (isSend) {
            TimeNewCons timeCons = TimeNewCons.getInstance();
            RecordJob.isSend = true;
            RecordJob.count.incrementAndGet();
            timeCons.start();
        }
        JSONObject jsonObject = new JSONObject().set("status", "OK");
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }





    private static void handleSendOnecMsg(ApplicationContext applicationContext, AbstractClientConnectionFactory client, String content) throws InterruptedException {
        boolean first = true;
        TcpConnectionSupport connection = client.getConnection();
        if ((connection.isOpen())){
            GatewayConstant.tcpPort.set(connection.getSocketInfo().getLocalPort());
            sendMessage(applicationContext, connection, content);
        }


    }
    private static void handleSingleJetcoTesting(ApplicationContext applicationContext, AbstractClientConnectionFactory client, String content) throws InterruptedException {
        boolean first = true;
        TcpConnectionSupport connection = client.getConnection();
        if ((connection.isOpen())){
            if(!first){

            }else {
                Thread.sleep(GatewayConstant.WAITING_FOR_SNA_AGENT_START);
            }
            while (connection.isOpen()){
                sendMessage(applicationContext, connection, content);
                Thread.sleep(5000);
            }
            first = false;
            connection.close();
        }
    }

    private static void sendMessage(ApplicationContext applicationContext, TcpConnectionSupport connection, String content) throws InterruptedException {
        MessageChannel clientTransformChannel = applicationContext.getBean("unionMockClientOutChannel",
                MessageChannel.class);
        byte[] bytes = content.getBytes(StandardCharsets.US_ASCII);
        byte[] res;
        if(GatewayConstant.IS_EBC_OR_ASCII){
            res = CpicUtil.convertToEbc(bytes, bytes.length);
        }else{
            res = bytes;
        }
        byte[] lenBytes = converContent(res).getBytes(StandardCharsets.US_ASCII);
        byte[] total = new byte[res.length + lenBytes.length];
        System.arraycopy(lenBytes, 0, total, 0, 4);
        System.arraycopy(res, 0, total, 4, res.length);
        Message<byte[]> message = MessageBuilder.withPayload(total)
                .setHeader(IpHeaders.CONNECTION_ID, connection.getConnectionId())
                .build();
        boolean isSend = clientTransformChannel.send(message);
        if (isSend) {
            TimeNewCons timeCons = TimeNewCons.getInstance();
            timeCons.start();
            RecordJob.isSend = true;
            RecordJob.count.incrementAndGet();
        }
    }

    public static String converContent(byte[] bytes) {
        String res = String.valueOf(bytes.length);
        String result = StringUtils.leftPad(res, 4, "0");
        return result;
    }


}
