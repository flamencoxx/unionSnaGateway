package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.model.ExitCodeEnum;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

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
}
