package com.iaspec.uniongatewaymock.unionGateway.client;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.model.TimeNewCons;
import com.iaspec.uniongatewaymock.serializer.ByteArraySerializer;
import com.iaspec.uniongatewaymock.util.CpicUtil;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  11:09
 */
public class UnionMessageHandle {
    public void handleMessage(Message<byte[]> message){
//        SystemLogger.infoMethod(getClass(), "handleRequestMessage",
//                (String) message.getHeaders().get(IpHeaders.CONNECTION_ID), true,
//                new String[] { "message.Headers" }, message.getHeaders());
//        SystemLogger.debug("message.payload.length={0}", message.getPayload().length);
        String remoteAddress = ((String) message.getHeaders().get(IpHeaders.IP_ADDRESS));
        byte[] data = message.getPayload();
        if (data.length == 0 || ByteArraySerializer.isAllZero(data)) {
            SystemLogger.info("Message maybe is wrong,data is empty",new String(data,StandardCharsets.US_ASCII));
            return;
        }
        byte[] msg = new byte[data.length - 4];
        byte[] msgLen = new byte[4];
        System.arraycopy(data, 0, msgLen, 0, 4);
        System.arraycopy(data,4,msg,0,msg.length);
        String str;
        if(GatewayConstant.IS_EBC_OR_ASCII){
            str = new String(CpicUtil.convertToAsc(data,data.length), StandardCharsets.US_ASCII);
        }else{
            str = new String(data, StandardCharsets.US_ASCII);
        }

        TimeNewCons timeCons = TimeNewCons.getInstance();
        long timer = timeCons.acceptMsg(str);

        SystemLogger.info("Accept Msg form gateway2, msg={0}",str);
//        GatewayConstant.CONNECTION_ID = (String) message.getHeaders().get(IpHeaders.CONNECTION_ID);
    }
}
