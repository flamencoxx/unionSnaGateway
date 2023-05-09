package com.iaspec.uniongatewaymock.config;

import cn.hutool.core.thread.ThreadUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.model.TimeNewCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.ip.tcp.connection.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Flamenco.xxx
 * @date 2022/9/23  16:42
 */
@Component
@DependsOn("unionMockServerFactory")
public class RecordJob implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    public static AtomicInteger count = new AtomicInteger(0);

    @Value("${mock.server.port}")
    private long serverPort = 0L;

    @Value("${mock.client.port}")
    private long clientPort = 0L;

    @Value("${isEbcOrAscii}")
    public boolean isEbcOrAscii = true;

    public static boolean isSend = false;

    ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("RecordJob-pool-%d")
            .build();

    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 3, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), factory, new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        GatewayConstant.IS_EBC_OR_ASCII = isEbcOrAscii;
        GatewayConstant.mockServerPort = serverPort;
        GatewayConstant.mockClientPort = clientPort;
        TcpNioServerConnectionFactory serverFactory = applicationContext.getBean("unionMockServerFactory", TcpNioServerConnectionFactory.class);
        serverFactory.registerSender(new UnionMockServerSender());

        GatewayConstant.clientFactory = applicationContext.getBean("unionMockClientFactory", TcpNioClientConnectionFactory.class);
        GatewayConstant.serverFactory = applicationContext.getBean("unionMockServerFactory", TcpNioServerConnectionFactory.class);
//        GatewayConstant.clientConnect = GatewayConstant.clientFactory.getConnection();
//
        isSend = true;

        poolExecutor.execute(new CountSendInfoJob());
        poolExecutor.execute(new MonitorTimerJob());
    }

     static class MonitorTimerJob implements Runnable {
        @Override
        public void run() {

            TimeNewCons timeCons = TimeNewCons.getInstance();
            while(true) {
                while(GatewayConstant.sendRecords.keySet().size() > 0){
                    long error = GatewayConstant.sendRecords.keySet()
                            .stream()
                            .map(k -> {
                                if (GatewayConstant.sendRecords.get(k) == null){
                                    return null;
                                }
                                if (GatewayConstant.sendRecords.get(k)
                                        .interval() > 5000) {
                                    timeCons.errorTimes.incrementAndGet();
                                    GatewayConstant.sendRecords.remove(k);
                                    return k;
                                }
                                return null;
                            })
                            .count();
                }
                ThreadUtil.sleep(500);
            }
        }
    }

    static class CountSendInfoJob implements Runnable {
        @Override
        public void run() {
            while (true) {
                if(isSend){
                    ThreadUtil.sleep(1200);
//                    System.out.println("==========================Count accept = " + count.get() + "=====All Accept=" + TimeCons.getInstance().sendCount.get());
                    count.set(0);
                    isSend = false;
                }
                ThreadUtil.sleep(300);
            }
        }
    }
}
