package com.iaspec.uniongatewayserver.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Flamenco.xxx
 * @date 2022/8/16  14:12
 */
public interface PropertyService {

    void reloadConfig();

    Properties loadClassPathProperty();




    Object getValue(String key, Object defaultValue);

    String getStringValue(String key, String defaultValue);

    String getDecryptedStringValue(String key, String defaultValue);

    String getEnvVarExpandedStringValue(String key, String defaultValue);

    Integer getIntegerValue(String key, Integer defaultValue);

    Short getShortValue(String key, Short defaultValue);

    Long getLongValue(String key, Long defaultValue);

    Float getFloatValue(String key, Float defaultValue);

    Double getDoubleValue(String key, Double defaultValue);

    Boolean getBooleanValue(String key, Boolean defaultValue);

    Date getTimeValue(String key, Date defaultValue);

    byte[] getBinaryValue(String key);

    Map<String, String> getIpAddressAndLuNamePropertiesMap();

    void setIpAddressAndLuNamePropertiesMap(Map<String, String> ipAddressAndLuNamePropertiesMap);
}
