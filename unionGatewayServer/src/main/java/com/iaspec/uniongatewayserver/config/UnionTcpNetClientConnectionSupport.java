package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import com.iaspec.uniongatewayserver.util.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.ip.tcp.connection.AbstractTcpConnectionSupport;
import org.springframework.integration.ip.tcp.connection.TcpNetConnection;
import org.springframework.integration.ip.tcp.connection.TcpNetConnectionSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.*;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Flamenco.xxx
 * @date 2023/4/17  16:16
 */
public class UnionTcpNetClientConnectionSupport extends AbstractTcpConnectionSupport implements TcpNetConnectionSupport {

    public static final String LOCALHOST = "127.0.0.1";

    public static final ConcurrentHashMap<String, Object> socketInfo = new ConcurrentHashMap<>();

    public static final String SO_TIMEOUT = "SoTimeout";

    public static final String SEND_BUFFER_SIZE = "SendBufferSize";

    public static final String RECEIVE_BUFFER_SIZE = "ReceiveBufferSize";

    public static final String KEEPALIVE = "KeepAlive";

    public static final String TCPNODELAY = "TcpNoDelay";

    public static final String SO_LINGER = "SoLinger";

    public static final String TRAFFIC_CLASS = "TrafficClass";

    public static final String REUSE_ADDRESS = "ReuseAddress";

    public static final AtomicInteger retryCount = new AtomicInteger(-1);


    public TcpNetConnection createNewConnection(@NonNull Socket socket, boolean server, boolean lookupHost, ApplicationEventPublisher applicationEventPublisher, @NonNull String connectionFactoryName) {
        Socket customSocket = null;
        Optional<Socket> socketOp = Optional.empty();
        InetAddress host;
        int port = GatewayConstant.CLIENT_REMOTE_PORT;
        InetAddress localHost;
        int localPort = GatewayConstant.CLIENT_LOCAL_PORT;
        try {
            checkConnectRetry();
            if (GatewayConstant.CLIENT_REMOTE_HOST.equals(LOCALHOST)) {
                host = InetAddress.getLocalHost();
            } else {
                host = InetAddress.getByName(GatewayConstant.CLIENT_REMOTE_HOST);
            }
            localHost = InetAddress.getLocalHost();
//            customSocket = new Socket(host, port, localHost, localPort);
            customSocket = new Socket();
            settingsSocket(socket, customSocket);
            getSocketInfo(socketInfo, customSocket);
            customSocket.bind(new InetSocketAddress(localHost,localPort));
            customSocket.connect(new InetSocketAddress(host,port),GatewayConstant.CLIENT_CONNECT_TIMEOUT);

//            tryConnectAndRetry(customSocket, host, port, localHost, localPort);
            socketOp = Optional.of(customSocket);
        } catch (NullPointerException e) {
            SystemLogger.error("Occurs error when create Socket, socket Object is null", new String[]{StringUtils.EMPTY}, new Throwable("socket Object is null"));
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            SystemLogger.error("Occur error(UnknownHostException) when create Socket Connection, e,message= {0}", new String[]{e.getMessage()}, e);
            throw new RuntimeException(e);
        } catch (BindException e) {
//            本地端口或对方server端口绑定失败关闭系统
            handleClientException(ExceptionEnum.BIND_EXCEPTION, true, e.getClass().getName(), e.getMessage(), e);
        } catch (ConnectException e) {
            handleClientException(ExceptionEnum.SOCKET_CONNECT_EXCEPTION,false,e.getClass().getName(),e.getMessage(),e);
        } catch (SocketTimeoutException e) {
            handleClientException(ExceptionEnum.SOCKET_CONNECT_TIMEOUT,false,e.getClass().getName(),e.getMessage(),e);
        } catch (SocketException e) {
            handleClientException(ExceptionEnum.SOCKET_EXCEPTION, false, e.getClass().getName(), e.getMessage(), e);
        } catch (IOException e) {
            SystemLogger.error("Occur error(IOException) when create Socket Connection, e,message= {0}", new String[]{e.getMessage()}, e);
            throw new RuntimeException(e);
        } catch (Throwable e) {
            SystemLogger.error("Occur error when create Socket Connection, e.message = {0}", new String[]{e.getMessage()}, e);
        }
        if (isPushbackCapable()) {
            return new UnionTcpNetClientConnectionSupport.PushBackTcpNetConnection(customSocket, server, lookupHost, applicationEventPublisher, connectionFactoryName, getPushbackBufferSize());
        } else {
            return socketOp.map(value -> new TcpNetConnection(value, server, lookupHost, applicationEventPublisher, connectionFactoryName))
                    .orElseGet(() -> {
                        handleClientException(ExceptionEnum.CREAT_TCP_CONNECTION_EXCEPTION, false, "CreatSocketFailure", "create TcpNetConnection failure", new Throwable());
                        return null;
                    });

        }
    }

    private void tryConnectAndRetry(Socket socket,InetAddress remoteHost,int remotePort,InetAddress localHost,int localPort) throws IOException {
        socket.bind(new InetSocketAddress(localHost,localPort));
        AtomicBoolean connected = new AtomicBoolean(false);
        AtomicInteger retryCount = new AtomicInteger(-1);

        while (!connected.get() && retryCount.incrementAndGet() < GatewayConstant.MAX_RETIES) {
            try {
//                SystemLogger.error("test socketTimeOut : {0},retryCount : {1},reUseAddress : {2}",socket.getSoTimeout(),retryCount.get(),socket.getReuseAddress());
                socket.connect(new InetSocketAddress(remoteHost,remotePort),GatewayConstant.CLIENT_CONNECT_TIMEOUT);
                connected.set(true);
            } catch (SocketTimeoutException e) {
                SystemLogger.error("Client connect timeout,connect retry {0} times",retryCount.get(),e);
                ThreadUtil.sleep(GatewayConstant.RETRY_INTERVAL);
                connected.set(false);
            } catch (ConnectException e){
                SystemLogger.error("Client connect exception,connect retry {0} times",retryCount.get(),e);
                ThreadUtil.sleep(GatewayConstant.RETRY_INTERVAL);
                connected.set(false);
            } catch (SocketException e){
                SystemLogger.error("SocketException,connect retry {0} times, msg : {1}",retryCount.get(),e.getMessage(),e);
                ThreadUtil.sleep(GatewayConstant.RETRY_INTERVAL);
                try {
                    if(socket.isClosed()){
                        socket.close();
                        socket = new Socket();
                        setSocketInfo(socketInfo,socket);
                        socket.bind(new InetSocketAddress(localHost,localPort));
                        socket.connect(new InetSocketAddress(remoteHost,remotePort),GatewayConstant.CLIENT_CONNECT_TIMEOUT);
                        SystemLogger.info("new socket,isConnect : {0}",socket.isConnected());
                    }
                } catch (Exception ex) {
                    SystemLogger.error("Occur a error when retry connect and create new socket");
                }
                connected.set(false);
            }
        }

        if (!connected.get()) {
            SystemLogger.error("Client retry connection exceeds maximum limit,connect retry {0} times",retryCount.get());
            handleClientException(ExceptionEnum.RETRY_CONNECT_OVER_LIMIT,true,"retryConnectFail",null,new Throwable());
        }
    }

    private void checkConnectRetry(){
        SystemLogger.info("client try connect {0} times",retryCount.incrementAndGet());
        if(retryCount.get() > GatewayConstant.MAX_RETIES){
            handleClientException(ExceptionEnum.RETRY_CONNECT_OVER_LIMIT,true,"retryConnectFail",null,new Throwable());
        }
    }


    private void closeSocket(Socket socket){
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            SystemLogger.error("Occur a error when close Socket");
        }
    }

    public void getSocketInfo(ConcurrentHashMap<String,Object> map, Socket socket) throws SocketException {
        map.put(SO_TIMEOUT, socket.getSoTimeout());
        map.put(SEND_BUFFER_SIZE, socket.getSendBufferSize());
        map.put(RECEIVE_BUFFER_SIZE, socket.getReceiveBufferSize());
        map.put(KEEPALIVE, socket.getKeepAlive());
        map.put(TCPNODELAY, socket.getTcpNoDelay());
        map.put(SO_LINGER, socket.getSoLinger());
        map.put(TRAFFIC_CLASS, socket.getTrafficClass());
        map.put(REUSE_ADDRESS, socket.getReuseAddress());
    }

    public void setSocketInfo(ConcurrentHashMap<String,Object> map, Socket socket) throws SocketException {
        socket.setSoTimeout((Integer) map.get(SO_TIMEOUT));
        socket.setSendBufferSize((Integer) map.get(SEND_BUFFER_SIZE));
        socket.setReceiveBufferSize((Integer) map.get(RECEIVE_BUFFER_SIZE));
        socket.setKeepAlive((Boolean) map.get(KEEPALIVE));
        socket.setTcpNoDelay((Boolean) map.get(TCPNODELAY));
        socket.setSoLinger(true, (Integer) map.get(SO_LINGER));
        socket.setTrafficClass((Integer) map.get(TRAFFIC_CLASS));
        socket.setReuseAddress((Boolean) map.get(REUSE_ADDRESS));
    }
    private void handleClientException(ExceptionEnum exceptionEnum,boolean isShutdown,String errorType,String errorMsg,Throwable e) {
        SystemLogger.error("Occur {0} when create Socket Connection, e,message= {1}", new String[]{errorType,e.getMessage()}, e);
        if (StringUtils.isNotBlank(errorMsg)){
            SystemLogger.error(errorMsg);
        }
        if (isShutdown){
            CommandUtils.runAbnormalShellWithFunc(exceptionEnum.getMsg(), s -> {
                SystemLogger.error("Abnormal shell fail to run,error msg : {0}", s);
                return null;
            });
            ExitSystemUtil.exitSystem(exceptionEnum, exceptionEnum.getMsg());
        }
    }


    private void settingsSocket(Socket sourceSocket, Socket destSocket) throws SocketException {
        if (sourceSocket.getSoTimeout() >= 0) {
            destSocket.setSoTimeout(sourceSocket.getSoTimeout());
        }
        if (sourceSocket.getSendBufferSize() > 0) {
            destSocket.setSendBufferSize(sourceSocket.getSendBufferSize());
        }
        if (sourceSocket.getReceiveBufferSize() > 0) {
            destSocket.setReceiveBufferSize(sourceSocket.getReceiveBufferSize());
        }
        destSocket.setKeepAlive(sourceSocket.getKeepAlive());
        destSocket.setTcpNoDelay(sourceSocket.getTcpNoDelay());
        if (sourceSocket.getSoLinger() >= 0) {
            destSocket.setSoLinger(true, sourceSocket.getSoLinger());
        }
        if (sourceSocket.getTrafficClass() >= 0) {
            destSocket.setTrafficClass(sourceSocket.getTrafficClass());
        }
//        自定义设置
        destSocket.setReuseAddress(true);
    }


    private static final class PushBackTcpNetConnection extends TcpNetConnection {

        private final int pushbackBufferSize;

        private final String connectionId;

        private volatile PushbackInputStream pushbackStream;

        private volatile InputStream wrapped;


        PushBackTcpNetConnection(Socket socket, boolean server, boolean lookupHost, @Nullable ApplicationEventPublisher applicationEventPublisher, String connectionFactoryName, int bufferSize) {

            super(socket, server, lookupHost, applicationEventPublisher, connectionFactoryName);
            this.pushbackBufferSize = bufferSize;
            this.connectionId = "pushback:" + super.getConnectionId();
        }

        @Override
        protected InputStream inputStream() throws IOException {
            InputStream wrappedStream = super.inputStream();
            if (this.pushbackStream == null || !wrappedStream.equals(this.wrapped)) {
                this.pushbackStream = new PushbackInputStream(wrappedStream, this.pushbackBufferSize);
                this.wrapped = wrappedStream;
            }
            return this.pushbackStream;
        }

        @Override
        public String getConnectionId() {
            return this.connectionId;
        }

    }
}
