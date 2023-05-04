package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Flamenco.xxx
 * @date 2023/4/14  14:36
 */
@Component
public class ExitApplicationJob {


    @Autowired
    private ApplicationContext applicationContext;


    @PreDestroy
    public void destroy() {
        if(!GatewayConstant.isAbnormalShutdown.get()){
            SystemLogger.info("Into Destroy method,prepare exit system");
            try {
                CommandUtils.runNormalShell();

            } catch (Throwable e) {
                SystemLogger.error("Occurs a error when destroy, errorMsg: {0}",new String[]{e.getMessage()},e);
            } finally {
                SystemLogger.info("destroy end");
            }
        }
    }
}
