package com.iaspec.uniongatewaymock.config;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Flamenco.xxx
 * @date 2022/11/1  15:12
 */
@Component
public class ExitApplicationJob {

    @Autowired
    private ApplicationContext applicationContext;

    @PreDestroy
    public void destory() {
        SystemLogger.info("Into Destroy method,prepare exit system");
        try {
            GatewayConstant.clientConnect.shutdownInput();
            GatewayConstant.clientConnect.shutdownOutput();
            GatewayConstant.clientConnect.close();
        } catch (Throwable e) {
            SystemLogger.error("Occurs a error when destory, errorMsg: {0}",new String[]{e.getMessage()},e);
        } finally {
            System.out.println("end destroy");
        }

    }
}
