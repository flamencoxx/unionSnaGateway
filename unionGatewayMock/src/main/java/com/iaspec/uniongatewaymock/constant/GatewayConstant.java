package com.iaspec.uniongatewaymock.constant;

import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Maps;
import com.iaspec.uniongatewaymock.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  10:49
 */
public class GatewayConstant {

    public static String mockServerConnectionId;

    public static boolean IS_EBC_OR_ASCII;

    public static Map<String, TimeInterval> sendRecords = Maps.newConcurrentMap();

    public static long WAITING_FOR_SNA_AGENT_START = 1000l;

    public static AtomicLong seqNo = new AtomicLong(0);

    public static volatile String CONNECTION_ID = StringUtils.EMPTY;

    public static AtomicLong tcpPort = new AtomicLong(0);

    public static long mockServerPort = 0L;

    public static long mockClientPort = 0L;

    public static String SERVER_REMOTE_HOST = StringUtils.EMPTY;

    public static int SERVER_REMOTE_PORT = 0;

    public static TcpNetClientConnectionFactory clientFactory;

    public static TcpConnection clientConnect;

    public static AtomicReference<String> IP_CONNECTION_ID = new AtomicReference<>();

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_1 = "spring.application_context_path.1";

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_2 = "spring.application_context_path.2";

    private static final String FILE_APPLICATION_CONTEXT_PATH_PREFIX = "file:${";

    private static final String FILE_APPLICATION_CONTEXT_PATH_SUBFIX = "}";

    public final static String GATEWAY_HOME = CommonUtils.expandEnvVars("${gateway.home}");


    public static final String FILE_APPLICATION_CONTEXT_PATH_1 = FILE_APPLICATION_CONTEXT_PATH_PREFIX
            + PROP_KEY_APPLICATION_CONTEXT_PATH_1 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

    public static final String FILE_APPLICATION_CONTEXT_PATH_2 = FILE_APPLICATION_CONTEXT_PATH_PREFIX
            + PROP_KEY_APPLICATION_CONTEXT_PATH_2 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

}
