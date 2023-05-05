package com.iaspec.uniongatewayserver.service.impl;

import com.google.common.collect.Maps;


import com.iaspec.uniongatewayserver.config.*;
import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.constant.GatewayConstantLoader;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.service.PropertyService;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.PropertyStore;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Flamenco.xxx
 * @date 2022/8/16  14:45
 */
@Order(0)
public class PropertyServiceImpl implements PropertyService, BeanNameAware {

    protected Properties allProp;

    //    union's  Ip Address and System Dest Name Map
    private Map<String, String> ipAddressAndSDNPropertiesMap;

    @Resource
    protected List<PropertyStore> propertyStores;

    @Autowired
    private ApplicationContext applicationContext;

    public String beanName;

    public void init() {
        SystemLogger.infoMethod(getClass(), "init", true, new String[]{});

        try {
            ipAddressAndSDNPropertiesMap = Maps.newConcurrentMap();
            allProp = new Properties();
            Properties clsProp = this.loadClassPathProperty();
            if (ObjectUtils.isNotEmpty(clsProp)) {
                allProp.putAll(clsProp);
            }
            LogAllProp();

            GatewayConstantLoader.reloadProperties(this);


            int serverPort = this.getIntegerValue(GatewayConstant.PROP_KEY_SERVER_PORT, 0);
            GatewayConstant.SERVER_LOCAL_PORT = serverPort;
            GatewayConstant.SERVER_LOCAL_HOST = InetAddress.getLocalHost()
                    .getHostAddress();
            GatewayConstant.SERVER_FACTORY = applicationContext.getBean(GatewayConstant.SERVER_FACTORY_NAME, AbstractServerConnectionFactory.class);
            GatewayConstant.SERVER_INBOUND_CHANNEL = applicationContext.getBean("unionServerInboundChannel", MessageChannel.class);
            GatewayConstant.SERVER_OUTBOUND_CHANNEL = applicationContext.getBean("unionServerOutboundChannel", MessageChannel.class);

            GatewayConstant.SERVER_FACTORY.registerSender(new UnionServerTcpSender());

            GatewayConstant.CLIENT_REMOTE_HOST = this.getStringValue(GatewayConstant.PROP_KEY_CLIENT_REMOTE_HOST, StringUtils.EMPTY);
            GatewayConstant.CLIENT_REMOTE_PORT = this.getIntegerValue(GatewayConstant.PROP_KEY_CLIENT_REMOTE_PORT, 0);
            GatewayConstant.CLIENT_LOCAL_PORT = this.getIntegerValue(GatewayConstant.PROP_KEY_CLIENT_LOCAL_PORT, 0);

            GatewayConstant.CLIENT_FACTORY = applicationContext.getBean(GatewayConstant.CLIENT_FACTORY_NAME, UnionTcpNetClientConnectionFactory.class);
            GatewayConstant.CLIENT_INBOUND_CHANNEL = applicationContext.getBean("unionClientInboundChannel", MessageChannel.class);
            GatewayConstant.CLIENT_OUTBOUND_CHANNEL = applicationContext.getBean("unionClientOutboundChannel", MessageChannel.class);

            GatewayConstant.CLIENT_FACTORY.registerSender(new UnionClientTcpSender());
            GatewayConstant.CLIENT_FACTORY.setTcpSocketFactorySupport(new UnionTcpNetSocketFactorySupport());
            GatewayConstant.CLIENT_FACTORY.setTcpNetConnectionSupport(new UnionTcpNetClientConnectionSupport());

            GatewayConstant.ERROR_CHANNEL = applicationContext.getBean("errorChannel", MessageChannel.class);




            if (ObjectUtils.anyNull(GatewayConstant.SERVER_FACTORY,
                    GatewayConstant.SERVER_INBOUND_CHANNEL,
                    GatewayConstant.SERVER_OUTBOUND_CHANNEL,
                    GatewayConstant.CLIENT_FACTORY,
                    GatewayConstant.CLIENT_INBOUND_CHANNEL,
                    GatewayConstant.CLIENT_OUTBOUND_CHANNEL)) {
                CommandUtils.runAbnormalShell("Factory or channel is null");
                ExitSystemUtil.exitSystem(ExceptionEnum.INIT_ERROR, "Factory or channel is null");
            }

            if (GatewayConstant.CLIENT_REMOTE_PORT == 0
                    || GatewayConstant.SERVER_LOCAL_PORT == 0
                    || GatewayConstant.CLIENT_LOCAL_PORT == 0
                    || StringUtils.equals(GatewayConstant.CLIENT_REMOTE_HOST,StringUtils.EMPTY)) {
                CommandUtils.runAbnormalShell("ServerPort,clientLocalPort,clientRemotePort or clientRemoteAddress is not config,Please check config");
                ExitSystemUtil.exitSystem(ExceptionEnum.INIT_ERROR, "ServerPort,clientLocalPort,clientRemotePort or clientRemoteAddress is not config,Please check config");
            }

            if (GatewayConstant.isDuplex.get()){
                SystemLogger.info("The system uses Duplex mode");
            }else {

                SystemLogger.info("The system uses the default mode Simplex mode");
            }



        } catch (Throwable e) {
            SystemLogger.error("Occurs a error {0} when init prop", new String[]{e.getMessage()}, e);
            ExitSystemUtil.exitSystem(ExceptionEnum.INIT_ERROR, "Exception Detail : " + e.getMessage());
        } finally {
        }

//        GatewayConstant.CLIENT_LOCAL_PORT = this.getIntegerValue(GatewayConstant.PROP_KEY_CLIENT_LOCAL_PORT, 0);
//        int clientPort = this.getIntegerValue(GatewayConstant.PROP_KEY_CLIENT_PORT,6002);
//        String clientHost = this.getStringValue(GatewayConstant.PROP_KEY_CLIENT_HOST, "127.0.0.1");
//        AbstractServerConnectionFactory serverFactory = applicationContext.getBean("jetcoConnectionFactory", AbstractServerConnectionFactory.class);
//        GatewayTcpNetClientConnectionFactory clientFactory = applicationContext.getBean("jetcoClientFactory", GatewayTcpNetClientConnectionFactory.class);
//
//        serverFactory.setPort(serverPort);
//        clientFactory.setHost(clientHost);
//        clientFactory.setPort(clientPort);
//
//        clientFactory.setTcpSocketFactorySupport(new GatewayTcpNetSocketFactorySupport());
//        clientFactory.setTcpNetConnectionSupport(new GatewayTcpNetClientConnectionSupport());
//
//        MessageChannel serverChannel = applicationContext.getBean(JETCO_CLIENT_RETURN_CHANNEL, MessageChannel.class);
//
//        if (ObjectUtils.isEmpty(serverChannel)){
//            SystemLogger.error("server channel or client channel is empty");
//        }
//        Map.Entry<String, MessageChannel> clientChannelMap = Maps.immutableEntry(JETCO_CLIENT_RETURN_CHANNEL, serverChannel);
//        GatewayConstant.GATEWAY_TO_JETCO_CHANNEL = new ListBalance<>(Lists.newArrayList(clientChannelMap));
////        加载GatewayConstant
//        GatewayConstantLoader.reloadProperties(this);

        SystemLogger.infoMethod(getClass(), "init", false, new String[]{});
    }

    @Override
    public void reloadConfig() {
        SystemLogger.infoMethod(getClass(), "reloadConfig", true, new String[]{});

        Properties allProperty = new Properties();
        Properties clsProp = this.loadClassPathProperty();
        if (clsProp != null) allProperty.putAll(clsProp);

        this.allProp = allProperty;

        LogAllProp();
//       加载GatewayConstant
        GatewayConstantLoader.reloadProperties(this);
        SystemLogger.infoMethod(getClass(), "reloadConfig", false, new String[]{});
    }

    @Override
    public Properties loadClassPathProperty() {
        Properties prop = new Properties();
        for (PropertyStore propertyStore : propertyStores) {
            propertyStore.reloadConfig();
            Properties p = propertyStore.getProp();
            if (ObjectUtils.isNotEmpty(p)) {
                prop.putAll(p);
            }
        }
        return prop;
    }


    @Override
    public Object getValue(String key, Object defaultValue) {
        String value = this.allProp.getProperty(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public String getStringValue(String key, String defaultValue) {
        return this.allProp.getProperty(key, defaultValue);
    }

    @Override
    public String getDecryptedStringValue(String key, String defaultValue) {
        return null;
    }

    @Override
    public String getEnvVarExpandedStringValue(String key, String defaultValue) {
        return null;
    }

    @Override
    public Integer getIntegerValue(String key, Integer defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Short getShortValue(String key, Short defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Short.parseShort(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Long getLongValue(String key, Long defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Float getFloatValue(String key, Float defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Double getDoubleValue(String key, Double defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        String value = this.allProp.getProperty(key);
        if (StringUtils.isBlank(value)) return defaultValue;
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Date getTimeValue(String key, Date defaultValue) {
        String value = this.getStringValue(key, null);
        if (value == null) return null;
        Date time;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        try {
            time = format.parse(value);
        } catch (java.text.ParseException e) {
            return null;
        }

        return time;
    }

    @Override
    public byte[] getBinaryValue(String key) {
        return new byte[4];
    }

    @Override
    public Map<String, String> getIpAddressAndLuNamePropertiesMap() {
        return ipAddressAndSDNPropertiesMap;
    }

    @Override
    public void setIpAddressAndLuNamePropertiesMap(Map<String, String> ipAddressAndSDNPropertiesMap) {

        if (ipAddressAndSDNPropertiesMap.isEmpty()) {
            this.ipAddressAndSDNPropertiesMap = Maps.newConcurrentMap();
        } else {
            this.ipAddressAndSDNPropertiesMap = ipAddressAndSDNPropertiesMap;
        }
    }

    private void LogAllProp() {
        // debug and display properties
        if (SystemLogger.isDebugEnabled()) {
            for (Map.Entry<Object, Object> entry : allProp.entrySet()) {
                SystemLogger.debug("[{0}] -> [{1}]", entry.getKey(), entry.getValue());
            }
            SystemLogger.debug("===== System Properties (End) =====");
        }
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
