package com.iaspec.uniongatewaymock.config;

import cn.hutool.core.lang.Console;
import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  11:14
 */
@Component
public class ConstantLoad implements ApplicationRunner {


    @Autowired
    private ApplicationContext applicationContext;

    @Value("${mock.server.port}")
    private long serverPort = 0L;

    @Value("${mock.client.port}")
    private long clientPort = 0L;

    @Value("${isEbcOrAscii}")
    public boolean isEbcOrAscii = true;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        GatewayConstant.IS_EBC_OR_ASCII = isEbcOrAscii;
        GatewayConstant.mockServerPort = serverPort;
        GatewayConstant.mockClientPort = clientPort;
        Console.log(GatewayConstant.mockServerPort);
//        TcpNetServerConnectionFactory serverConnectionFactory = applicationContext.getBean("unionMockServerFactory", TcpNetServerConnectionFactory.class);
//        serverConnectionFactory.registerSender(new UnionMockServerSender());
    }
}
