package com.iaspec.uniongatewayserver.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;


import com.iaspec.uniongatewayserver.service.CpicService;
import com.iaspec.uniongatewayserver.service.PropertyService;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import com.iaspec.uniongatewayserver.util.ThreadUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Flamenco.xxx
 * @date 2022/8/17  17:08
 */

@Component
public class AcceptDataJob implements ApplicationRunner {

    @Resource
    private CpicService cpicService;

    @Resource
    private PropertyService propertyService;

    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Accept SNA Data-task-%d")
            .build();

    ThreadPoolExecutor executorPool = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());


    @Override
    public void run(ApplicationArguments args) throws Exception {
        SystemLogger.infoMethod(getClass(), "run", true, new String[]{"args"}, args);
        try {

//            为了模拟测试环境暂时注释，后续需要打开
//            cpicService.acceptMessageFromMainFrame();

//            new Thread(() -> {
//                ThreadUtil.sleep(10000);
//                ExitSystemUtil.closeFactorysConnect();
//            }).start();

        } catch (Exception e) {
            SystemLogger.error("Occurs error in run Accept SNA Data-task, e.message = {0}", new String[]{e.getMessage()}, e);
        } finally {
            SystemLogger.infoMethod(getClass(), "run", false, new String[]{"args"}, args);
        }
    }
}

//            ThreadUtil.sleep(40 * 1000);
//            for (int i = 0; i < 4; i++) {
//                executorPool.execute(() -> {
//                    try {
//                        cpicService.checkReturnCode(new CPICReturnCode(2));
//                    } catch (ServiceException e) {
//
//                    }
//                });
//
//            }