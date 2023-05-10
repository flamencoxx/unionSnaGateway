package com.iaspec.uniongatewaymock.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * @author Flamenco.xxx
 * @date 2023/5/9  14:36
 */
@Controller
public class ErrorMockController {


    @Autowired
    private ApplicationContext applicationContext;


    @RequestMapping(value = "/shutdownMock", method = RequestMethod.POST)
    public void shutdownMock() throws InterruptedException, IOException {
        SystemLogger.info("into exit system, mockServerConnectionId = {0}",GatewayConstant.mockServerConnectionId);
//        GatewayConstant.clientFactory.getConnection().shutdownOutput();
//        GatewayConstant.clientFactory.getConnection().shutdownInput();
        GatewayConstant.clientFactory.getConnection().close();
        GatewayConstant.serverFactory.closeConnection(GatewayConstant.mockServerConnectionId);
        GatewayConstant.clientFactory.stop();
        GatewayConstant.serverFactory.stop();
        ThreadUtil.sleep(5000);
//        SystemLogger.info("factory state ,client factory : {0} ,server factory : {1}",GatewayConstant.clientFactory.getConnection().isOpen(),GatewayConstant.serverFactory.isRunning());
        SystemLogger.info("Call JVM shutdown hook method");
        System.exit(0);
    }
    @RequestMapping(value = "/shutdownClientFactory", method = RequestMethod.POST)
    public void shutdownClientFactory(){
        GatewayConstant.clientFactory.stop();
    }

    @RequestMapping(value = "closeClientConnect",method = RequestMethod.POST)
    public void closeClientConnect(){
        try {
            GatewayConstant.clientFactory.getConnection().close();
        } catch (InterruptedException e) {
            SystemLogger.error("Occur a error when close client connect", new String[]{e.getMessage()}, e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "shutdownServerFactory",method = RequestMethod.POST)
    public ResponseEntity<Boolean> shutdownServerFactory(){
        SystemLogger.info("shutdown mock server connection ,connectionId : {0}",GatewayConstant.mockServerConnectionId);
        SystemLogger.info("server ConnectionIdList : {0}",GatewayConstant.serverFactory.getOpenConnectionIds().toString());
        GatewayConstant.serverFactory.closeConnection(GatewayConstant.mockServerConnectionId);
        SystemLogger.info("server after ConnectionIdList : {0}",GatewayConstant.serverFactory.getOpenConnectionIds().toString());
        return ResponseEntity.ok(true);
    }

    @RequestMapping(value = "closeServerConnect",method = RequestMethod.POST)
    public ResponseEntity<Boolean> closeServerConnect(){
        SystemLogger.info("into closeServerConnect connect id : {0}",GatewayConstant.mockServerConnectionId);
        GatewayConstant.serverFactory.closeConnection(GatewayConstant.mockServerConnectionId);
        return ResponseEntity.ok(true);
    }



}
