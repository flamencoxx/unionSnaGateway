package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;

/**
 * @author Flamenco.xxx
 * @date 2023/5/4  11:16
 */
@Configuration
@EnableIntegration
public class AdapterConfig implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "unionClientInboundAdapter")
    @Conditional(IsDuplexMode.class)
    @DependsOn({GatewayConstant.CLIENT_FACTORY_NAME,"unionClientInboundChannel"})
    @Order(3)
    public TcpReceivingChannelAdapter unionClientInboundAdapter() {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        TcpNetClientConnectionFactory factory = applicationContext.getBean(GatewayConstant.CLIENT_FACTORY_NAME, TcpNetClientConnectionFactory.class);
        adapter.setConnectionFactory(factory);
        adapter.setOutputChannelName("unionClientInboundChannel");
        adapter.setErrorChannel(GatewayConstant.ERROR_CHANNEL);
        adapter.setClientMode(false);
        return adapter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        GatewayConstant.CLIENT_INBOUND_CHANNEL_ADAPTER = unionClientInboundAdapter();
    }

//    @Bean(name = "unionServerOutboundAdapter")
//    @Conditional(IsDuplexMode.class)
//    @DependsOn(GatewayConstant.SERVER_FACTORY_NAME)
//    @Order(3)
//    public TcpSendingMessageHandler unionServerOutboundAdapter(){
//        TcpSendingMessageHandler adapter = new TcpSendingMessageHandler();
//        TcpNioServerConnectionFactory factory = applicationContext.getBean(GatewayConstant.SERVER_FACTORY_NAME, TcpNioServerConnectionFactory.class);
//        adapter.setConnectionFactory(factory);
//        adapter.setClientMode(false);
//        return adapter;
//    }

    static class IsDuplexMode implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Boolean.TRUE.equals(context.getEnvironment()
                    .getProperty(GatewayConstant.PROP_KEY_IS_DUPLEX, Boolean.class));
        }
    }
}
