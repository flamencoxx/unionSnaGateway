package com.iaspec.uniongatewayserver.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  17:53
 */
@ControllerAdvice
public class GlobalExceptionHandler {


//    @ExceptionHandler(value = ServiceException.class)
//    @ResponseBody
//    public  ResultBody serviceExceptionHandler(HttpServletRequest req, BizException e){
//        logger.error("发生业务异常！原因是：{}",e.getErrorMsg());
//        return ResultBody.error(e.getErrorCode(),e.getErrorMsg());
//    }
}
