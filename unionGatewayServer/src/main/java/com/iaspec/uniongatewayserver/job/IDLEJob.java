package com.iaspec.uniongatewayserver.job;

import com.iaspec.uniongatewayserver.service.CpicService;
import com.iaspec.uniongatewayserver.service.impl.CpicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author Flamenco.xxx
 * @date 2023/6/14  14:33
 */
@Component
public class IDLEJob {

    @Autowired
    private CpicService cpicService;

    public static final String IDLE_MSG = "";

    @PostConstruct
    public void init() {

    }

    @Scheduled(cron = "${idleCron}")
    private void idleJob() {
        CpicServiceImpl.client2UMPS(IDLE_MSG.getBytes(StandardCharsets.US_ASCII));
        CpicServiceImpl.server2UMPS(IDLE_MSG.getBytes(StandardCharsets.US_ASCII));
    }

}
