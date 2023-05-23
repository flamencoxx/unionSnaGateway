package com.iaspec.uniongatewayserver.config;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import com.iaspec.uniongatewayserver.util.ThreadUtil;
import com.sun.istack.internal.NotNull;
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

/**
 * @author Flamenco.xxx
 * @date 2023/4/17  16:16
 */
public class UnionTcpNetClientConnectionSupport extends AbstractTcpConnectionSupport implements TcpNetConnectionSupport {

    public static final String LOCALHOST = "127.0.0.1";


    public TcpNetConnection createNewConnection(@NonNull Socket socket, boolean server, boolean lookupHost, ApplicationEventPublisher applicationEventPublisher, @NonNull String connectionFactoryName) {
        Socket customSocket = null;
        Optional<Socket> socketOp = Optional.empty();
        InetAddress host;
        int port = GatewayConstant.CLIENT_REMOTE_PORT;
        InetAddress localHost;
        int localPort = GatewayConstant.CLIENT_LOCAL_PORT;
        try {
            if (GatewayConstant.CLIENT_REMOTE_HOST.equals(LOCALHOST)) {
                host = InetAddress.getLocalHost();
            } else {
                host = InetAddress.getByName(GatewayConstant.CLIENT_REMOTE_HOST);
            }
            localHost = InetAddress.getLocalHost();
            customSocket = new Socket(host, port, localHost, localPort);
            settingsSocket(socket, customSocket);
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
            handleClientException(ExceptionEnum.SOCKET_CONNECT_EXCEPTION,true,e.getClass().getName(),e.getMessage(),e);
        } catch (SocketTimeoutException e) {
            handleClientException(ExceptionEnum.SOCKET_CONNECT_TIMEOUT,false,e.getClass().getName(),e.getMessage(),e);
        } catch (SocketException e) {
            handleClientException(ExceptionEnum.SOCKET_EXCEPTION, true, e.getClass().getName(), e.getMessage(), e);
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
                        handleClientException(ExceptionEnum.CREAT_TCP_CONNECTION_EXCEPTION, true, "CreatSocketFailure", "Creat TcpNetConnection failure,Socket params is Empty,Prepare to close system", new Throwable());
                        return null;
                    });

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
