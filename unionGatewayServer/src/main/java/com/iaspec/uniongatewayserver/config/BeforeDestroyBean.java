package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  11:23
 * 在ClientFactory和ServerFactory 的Bean销毁前需要手动关闭connect
 */
@Component
public class BeforeDestroyBean implements DisposableBean {
    @Override
    public void destroy(){
        SystemLogger.info("====================");
        SystemLogger.info(GatewayConstant.SERVER_FACTORY.toString());
        SystemLogger.info(GatewayConstant.CLIENT_FACTORY.toString());
    }
}
