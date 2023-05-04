package com.iaspec.uniongatewayserver.exception;

import com.iaspec.uniongatewayserver.service.BaseErrorInfoInterface;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  18:05
 */
public enum UnionErrorEnum implements BaseErrorInfoInterface {


    SUCCESS("200", "success"),
    CONNECTION_EXCEPTION("","connection exception"), 
    CONFIGURATION_PARAMETER_EXCEPTIONS("", "Configuration parameter exceptions,Please Check config file"),

    SERVICE_EXCEPTION("","Service Exception")
    ;

    private String resultCode;

    private String resultMsg;

    UnionErrorEnum(String resultCode,String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }


    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
