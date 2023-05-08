package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.model.ExitCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Flamenco.xxx
 * @date 2023/4/20  9:28
 */
public class ExitSystemUtil {


    public synchronized static void exitSystem(ExceptionEnum exceptionEnum, String detailMsg) {
        try {
            SystemLogger.error("System ready to exit, exitType : {0} , {1} ,detailMsg : {2}", exceptionEnum.name(), exceptionEnum.getMsg(),detailMsg);
            GatewayConstant.isAbnormalShutdown.set(true);
            Thread exitThread = new Thread(() -> {
                SystemLogger.info("Call JVM shutdown hook method");
                System.exit(0);
            });
            exitThread.setName("exit-thread");
            exitThread.setDaemon(true);
            exitThread.start();
        } catch (Throwable e) {
            SystemLogger.error("System exit error", e);
        } finally {
            Thread shutdownThread = new Thread(() -> {
                try {
                    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                    String jvmName = bean.getName();
                    long pid = Long.parseLong(jvmName.split("@")[0]);
                    SystemLogger.info("PID  = " + pid);
                    Thread.sleep(30000);
                    SystemLogger.info("Call JVM shutdown hook method occur exception,Force System shutdown");
                } catch (Throwable e) {
                    SystemLogger.error("System shutdown Exception");
                } finally {
                    Runtime.getRuntime()
                            .halt(-1);
                }
            });
            shutdownThread.setName("shutdown-thread");
            shutdownThread.setDaemon(true);
            shutdownThread.start();
        }
    }

    public static void closeFactorysConnect(){
        AtomicLong waitTime = new AtomicLong(0);
        try {
            if (GatewayConstant.CLIENT_FACTORY.isRunning() && GatewayConstant.CLIENT_FACTORY.getConnection()
                    .isOpen() && GatewayConstant.SERVER_FACTORY.isRunning()) {
                GatewayConstant.CLIENT_FACTORY.getConnection().shutdownInput();
                if(GatewayConstant.CLIENT_INBOUND_CHANNEL.getQueueSize() != 0 || GatewayConstant.SERVER_INBOUND_CHANNEL.getQueueSize() != 0){
                    SystemLogger.info("Client or Server channel have {0} Message, waiting 3 second to handle msg",GatewayConstant.CLIENT_INBOUND_CHANNEL.getQueueSize() + GatewayConstant.SERVER_INBOUND_CHANNEL.getQueueSize());
                    waitTime.set(15000);
                }else {
                    GatewayConstant.CLIENT_FACTORY.getConnection().shutdownOutput();
                }

                ThreadUtil.sleep(waitTime.get());
                SystemLogger.info("waiting end,close Client and Server Connect");
                GatewayConstant.CLIENT_FACTORY.getConnection().shutdownOutput();
                GatewayConstant.CLIENT_FACTORY.closeConnection(GatewayConstant.CLIENT_CONNECTION_ID.get());
                GatewayConstant.SERVER_FACTORY.closeConnection(GatewayConstant.SERVER_CONNECTION_ID.get());

            }
        } catch (Throwable e) {
            SystemLogger.error("Occur a error when close client factory connect",new String[]{e.getMessage()},e);
        }

    }
}
