package com.iaspec.uniongatewayserver.model;

/**
 * @author Flamenco.xxx
 * @date 2023/4/20  9:29
 */
public enum ExceptionEnum {



    CONFIGURATION_EXCEPTION("Missing or incorrect configuration parameters, please check the gatewayProperties file or application.properties"),
    CONTINUOUS_EXCEPTION_RETURN_CODE("Consecutive exception return codes are accepted from SNA,The return code means that in addition to the blacklist and whitelist"),
    FATAL_ERROR("Received a fatal error, belonging to the blacklist return code"),
    INIT_ERROR("Initialization failure"),
    CREAT_TCP_CONNECTION_EXCEPTION("tcp connect failure"),

    SOCKET_CONNECT_EXCEPTION("connect exception"),

    BIND_EXCEPTION("Bind port failure,Check if the port is occupied"),

    SOCKET_EXCEPTION("socket creat or connect failure"),

    SOCKET_CONNECT_TIMEOUT("socket connect timeout"),

    SYSTEM_NAME_EMPTY("Missing key parameters system dest name,please check"),

    WHITE_LIST_EMPTY("Missing key parameters white list,please check")
    ;

    private final String Msg;

    ExceptionEnum(String msg){
        this.Msg = msg;
    }

    public String getMsg(){
        return this.Msg;
    }


}
