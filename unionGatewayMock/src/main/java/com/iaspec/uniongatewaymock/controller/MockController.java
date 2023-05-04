package com.iaspec.uniongatewaymock.controller;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.connection.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  14:56
 */
@Controller
public class MockController {

    @Autowired
    private ApplicationContext applicationContext;
    @RequestMapping(value = "/closeConnect", method = RequestMethod.POST)
    public void closeConnect() throws InterruptedException, IOException {
        TcpNetClientConnectionFactory factory = applicationContext.getBean("unionMockClientFactory", TcpNetClientConnectionFactory.class);
        factory.getConnection().shutdownOutput();
        factory.getConnection().shutdownInput();
        factory.getConnection().close();
//        factory.closeConnection(GatewayConstant.IP_CONNECTION_ID.get());
    }

    @RequestMapping(value = "/Connect", method = RequestMethod.POST)
    public void Connect() throws InterruptedException {
        TcpNetClientConnectionFactory factory = applicationContext.getBean("unionMockClientFactory", TcpNetClientConnectionFactory.class);
        TcpConnectionSupport connection = factory.getConnection();
        SystemLogger.info("connect is {0}",connection.isOpen());
    }


    @RequestMapping(value = "/TEST1", method = RequestMethod.POST)
    public void test1() {
        TcpNetServerConnectionFactory factory = applicationContext.getBean("unionMockServerFactory", TcpNetServerConnectionFactory.class);
        SystemLogger.info("socket is {0}");

    }

}
