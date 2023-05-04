package com.iaspec.uniongatewayserver.exception;

import com.iaspec.uniongatewayserver.service.BaseErrorInfoInterface;
import lombok.Data;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  17:55
 */
@Data
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public GlobalException(){
        super();
    }

    public GlobalException(String errorMsg){
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public GlobalException(String errorMsg,String errorCode){
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public GlobalException(String errorMsg,String errorCode,Throwable e){
        super(errorMsg,e);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;

    }

    public GlobalException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultMsg());
        this.errorMsg = errorInfoInterface.getResultMsg();
        this.errorCode = errorInfoInterface.getResultCode();
    }

    public GlobalException(BaseErrorInfoInterface errorInfoInterface, Throwable e) {
        super(errorInfoInterface.getResultMsg(),e);
        this.errorMsg = errorInfoInterface.getResultMsg();
        this.errorCode = errorInfoInterface.getResultCode();
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }


}
