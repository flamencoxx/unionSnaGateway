package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;

import java.math.BigInteger;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  17:59
 */
public class RecordUtil {

    public static void umps2GatewayRecord(){
        GatewayConstant.ACCEPT_MSG_COUNT = GatewayConstant.ACCEPT_MSG_COUNT.add(BigInteger.ONE);
    }

    public static void Gateway2UmpsRecord(){
        GatewayConstant.SEND_MSG_COUNT = GatewayConstant.SEND_MSG_COUNT.add(BigInteger.ONE);
    }

}
