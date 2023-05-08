package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  11:23
 * @info 在ClientFactory和ServerFactory 的Bean销毁前需要手动关闭connect
 */
@Component
public class BeforeDestroyBean implements DisposableBean {
    @Override
    public void destroy(){
//        看情况决定是否需要调整
        ExitSystemUtil.closeFactorysConnect();
    }
}
