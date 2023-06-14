package com.iaspec.uniongatewayserver.job;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.service.impl.CpicServiceImpl;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Flamenco.xxx
 * @date 2023/6/14  14:49
 */
@Component
public class MonitorMsgJob {

    public static final AtomicInteger currentMsgCount = new AtomicInteger(0);

    @PostConstruct
    public void init() {

    }

    @Scheduled(cron = "${monitorMsgCron}")
    private void monitorJob() {
        try {
            if (currentMsgCount.get() == 0) {
                SystemLogger.error("Since no messages including IDLE msg have been received for some time,the client's connection will be closed");

            }else {
                currentMsgCount.set(0);
                SystemLogger.info("IDLE msg checking is pass");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        catch (InterruptedException e) {
//            SystemLogger.error("Occur a error when monitorJob, msg : {0}", e.getMessage());
//        }
    }


}
