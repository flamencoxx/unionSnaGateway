package com.iaspec.uniongatewayserver.model;

import lombok.Data;

import javax.sound.sampled.Port;
import java.math.BigInteger;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  17:25
 */
@Data
public class GatewayInfo {

    private String localAddress;

    private int localPort;

    private String remoteAddress;

    private int remotePort;

    private String ipConnectionID;

    private int serverOpenConnectTimes;


    private int serverCloseConnectTimes;


    private String acceptMsgCount;

    private String sendMsgCount;

}
