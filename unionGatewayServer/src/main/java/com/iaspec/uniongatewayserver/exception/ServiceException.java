package com.iaspec.uniongatewayserver.exception;


import com.iaspec.uniongatewayserver.constant.GatewayConstant;

/**
 * @author Flamenco.xxx
 * @date 2022/8/12  17:40
 */



public class ServiceException extends Exception {

    private static final long serialVersionUID = 6093425926957507047L;
    private String errorCode;
    private String detail;
    private LogLevel logLevel = LogLevel.WARNING;

    public ServiceException() {
        this(GatewayConstant.EXCEPTION_FOUND_BUT_REASON_UNKNOWN);

    }

    public ServiceException(String errorCode) {
        super("[" + errorCode + "]");

        this.errorCode = errorCode;
    }

    public ServiceException(String errorCode, LogLevel logLv) {
        this(errorCode);
        this.logLevel = logLv;
    }

    public ServiceException(String errorCode, String detail) {
        super("[" + errorCode + "] - " + detail);

        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ServiceException(String errorCode, String detail, LogLevel logLv) {
        this(errorCode, detail);
        this.logLevel = logLv;
    }

    public ServiceException(String code, Throwable cause) {
        super("[" + code + "]", cause);

        this.errorCode = code;
    }

    public ServiceException(String errorCode, Throwable cause, LogLevel logLv) {
        this(errorCode, cause);
        this.logLevel = logLv;
    }

    public ServiceException(String code, String detail, Throwable cause) {
        super("[" + code + "] - " + detail, cause);

        this.errorCode = code;
        this.detail = detail;
    }

    public ServiceException(String errorCode, String detail, Throwable cause, LogLevel logLv) {
        this(errorCode, detail, cause);
        this.logLevel = logLv;
    }

    public ServiceException(Throwable cause) {
        this(GatewayConstant.EXCEPTION_FOUND_BUT_REASON_UNKNOWN, cause);

    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     *            - the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail
     *            - the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public static enum LogLevel {
        INFO, WARNING, ERROR, DEBUG
    }
}

