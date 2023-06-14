package com.iaspec.uniongatewayserver.constant;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.iaspec.uniongatewayserver.config.UnionTcpNetClientConnectionFactory;
import com.iaspec.uniongatewayserver.model.Holder;
import com.iaspec.uniongatewayserver.service.impl.CpicServiceImpl;
import com.iaspec.uniongatewayserver.util.CommonUtils;
import com.iaspec.uniongatewayserver.util.ListBalance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.messaging.MessageChannel;





import java.io.File;
import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  15:10
 */
public class GatewayConstant {


    public static final String SERVER_FACTORY_NAME = "unionServerFactory";
    public static String SERVER_LOCAL_HOST = StringUtils.EMPTY;
    public static int SERVER_LOCAL_PORT = 0;

    public static String SERVER_REMOTE_HOST = StringUtils.EMPTY;

    public static int SERVER_REMOTE_PORT = 0;

    public static boolean isServerConnect = false;

    public static final String CLIENT_FACTORY_NAME = "unionClientFactory";

    public static String CLIENT_REMOTE_HOST = StringUtils.EMPTY;

    public static int CLIENT_REMOTE_PORT = 0;

    public static int CLIENT_LOCAL_PORT = 0;

    public static boolean isClientConnect = false;

    public static AtomicReference<String> SERVER_CONNECTION_ID = new AtomicReference<>();

    public static AtomicReference<String> CLIENT_CONNECTION_ID = new AtomicReference<>();

    public final static String GATEWAY_HOME = CommonUtils.expandEnvVars("${gateway.home}");

    public static long TIME_INTERVAL_LONG = 500L;

    public static long TIME_INTERVAL_SHORT = 50L;

    public static int ERROR_COUNT_LIMIT = 10;

    public static String SYSTEM_DEST_NAME = StringUtils.EMPTY;

    public static String FATAL_ERROR_STR = StringUtils.EMPTY;

    public static String WHITE_LIST_STR = StringUtils.EMPTY;

    public static Integer REQUEST_LEN = 0;

    public static Set<Integer> fatalErrorSet = Sets.newHashSet();

    public static Set<Integer> whiteListCode = Sets.newHashSet();

    public static boolean IS_EBC_OR_ASCII;

    public static AtomicInteger RETURN_CODE_ERROR_COUNT = new AtomicInteger(0);

    public static AbstractServerConnectionFactory SERVER_FACTORY;

    public static QueueChannel SERVER_INBOUND_CHANNEL;

    public static MessageChannel SERVER_OUTBOUND_CHANNEL;

    public static UnionTcpNetClientConnectionFactory CLIENT_FACTORY;

    public static QueueChannel CLIENT_INBOUND_CHANNEL;

    public static MessageChannel CLIENT_OUTBOUND_CHANNEL;

    public static TcpReceivingChannelAdapter CLIENT_INBOUND_CHANNEL_ADAPTER;

    public static PollingConsumer SERVER_OUTBOUND_CHANNEL_ADAPTER;

    public static MessageChannel ERROR_CHANNEL;


    public static String NORMAL_SHELL = StringUtils.EMPTY;


    //    info
    public static BigInteger ACCEPT_MSG_COUNT = BigInteger.valueOf(0);

    public static BigInteger SEND_MSG_COUNT = BigInteger.valueOf(0);


    public static AtomicInteger SERVER_OPEN_CONNECT_TIMES = new AtomicInteger();

    public static AtomicInteger SERVER_CLOSE_CONNECT_TIMES = new AtomicInteger();

    public static AtomicInteger CLIENT_OPEN_CONNECT_TIMES = new AtomicInteger();

    public static AtomicInteger CLIENT_CLOSE_CONNECT_TIMES = new AtomicInteger();

    public static final Holder<File> abnormalShell = new Holder<>();

    public static final Holder<File> normalShell = new Holder<>();

    public static final AtomicBoolean isHaveCall = new AtomicBoolean(false);

    public static final AtomicBoolean isAbnormalShutdown = new AtomicBoolean(false);

    public static final AtomicBoolean isDuplex = new AtomicBoolean(false);

    public static int MAX_RETIES = 0;

    public static long RETRY_INTERVAL = 5000L;

    public static int CLIENT_CONNECT_TIMEOUT = 5000;


    public static final ListBalance<Function<byte[], Boolean>> SEND_FUNC = new ListBalance<>(Lists.newArrayList(CpicServiceImpl::client2UMPS,CpicServiceImpl::server2UMPS));


    // ======================================================================================

    public static final String PROP_KEY_CLIENT_CONNECT_TIMEOUT = "client.connect.timeout";

    public static final String PROP_KEY_MAX_RETIES = "maxRetries";

    public static final String PROP_KEY_RETRY_INTERVAL = "retryInterval";
    public static final String ClIENT_EXECUTOR_NAME = "clientExecutor";

    public static final String SERVER_EXECUTOR_NAME = "serverExecutor";

    public static final String PROP_KEY_CLIENT_REMOTE_HOST = "client.remote.host";

    public static final String PROP_KEY_CLIENT_REMOTE_PORT = "client.remote.port";

    public static final String PROP_KEY_CLIENT_LOCAL_PORT = "client.local.port";

    public static final String PROP_KEY_SERVER_PORT = "gateway.server.port";

    public static final String JAVA_D_SYSTEM_DEST_NAME = "systemDestName";

    public static final String PROP_KEY_SYSTEM_DEST_NAME = "system.dest.name";

    public static final String PROP_KEY_REQUEST_LEN = "requestLen";

    public static final String PROP_KEY_NORMAL_RETURN_CODE = "normalReturnCode";

    public static final String PROP_KEY_ERROR_COUNT_LIMIT = "error.limit";
    public static final String PROP_KEY_FATAL_ERROR = "fatalError";

    public static final String PROP_KEY_IS_EBC = "isEbcOrAscii";

    public static final String PROP_KEY_ABNORMAL_SHELL_PATH = "abnormal.shutdown.shell";

    public static final String PROP_KEY_NORMAL_SHELL_PATH = "normal.shutdown.shell";

    public static final String PROP_KEY_IS_DUPLEX = "isDuplex";

    private static final String FILE_APPLICATION_CONTEXT_PATH_PREFIX = "file:${";

    private static final String FILE_APPLICATION_CONTEXT_PATH_SUBFIX = "}";


    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_1 = "spring.application_context_path.1";

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_2 = "spring.application_context_path.2";

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_3 = "spring.application_context_path.3";

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_4 = "spring.application_context_path.4";

    public static final String PROP_KEY_APPLICATION_CONTEXT_PATH_5 = "spring.application_context_path.5";


    public static final String FILE_APPLICATION_CONTEXT_PATH_1 = FILE_APPLICATION_CONTEXT_PATH_PREFIX + PROP_KEY_APPLICATION_CONTEXT_PATH_1 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

    public static final String FILE_APPLICATION_CONTEXT_PATH_2 = FILE_APPLICATION_CONTEXT_PATH_PREFIX + PROP_KEY_APPLICATION_CONTEXT_PATH_2 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

    public static final String FILE_APPLICATION_CONTEXT_PATH_3 = FILE_APPLICATION_CONTEXT_PATH_PREFIX + PROP_KEY_APPLICATION_CONTEXT_PATH_3 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

    public static final String FILE_APPLICATION_CONTEXT_PATH_4 = FILE_APPLICATION_CONTEXT_PATH_PREFIX + PROP_KEY_APPLICATION_CONTEXT_PATH_4 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;

    public static final String FILE_APPLICATION_CONTEXT_PATH_5 = FILE_APPLICATION_CONTEXT_PATH_PREFIX + PROP_KEY_APPLICATION_CONTEXT_PATH_5 + FILE_APPLICATION_CONTEXT_PATH_SUBFIX;




    public static final String PROP_KEY_TIME_INTERVAL_LONG = "time.interval.long";

    public static final String PROP_KEY_TIME_INTERVAL_SHORT = "time.interval.short";





    public final static String ERROR_UNCLASSIFIED_PROBLEM = "G00005";

    public final static String EXCEPTION_FOUND_BUT_REASON_UNKNOWN = "G00007";

    public final static String ERROR_UNEXPECTED_PROBLEM = "G00006";

}
