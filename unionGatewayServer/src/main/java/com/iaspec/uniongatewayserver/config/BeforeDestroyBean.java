package com.iaspec.uniongatewayserver.config;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author Flamenco.xxx
 * @date 2023/4/19  11:23
 * 在ClientFactory和ServerFactory 的Bean销毁前需要手动关闭connect
 */
public class BeforeDestroyBean implements DisposableBean {
    @Override
    public void destroy() throws Exception {

    }
}
