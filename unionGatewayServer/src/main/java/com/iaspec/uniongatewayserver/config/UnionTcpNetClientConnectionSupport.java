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
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.*;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Flamenco.xxx
 * @date 2023/4/17  16:16
 */
public class UnionTcpNetClientConnectionSupport extends AbstractTcpConnectionSupport implements TcpNetConnectionSupport {

    public static final String LOCALHOST = "127.0.0.1";


    public TcpNetConnection createNewConnection(Socket socket, boolean server, boolean lookupHost, ApplicationEventPublisher applicationEventPublisher, String connectionFactoryName) {
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

            SystemLogger.info("Gateway Client Create New Connection, host={0}, port={1}, localHost={2}, localPort={3}", host, port, localHost, localPort);

        } catch (NullPointerException e) {
            SystemLogger.error("Occurs error when create Socket, socket Object is null", new String[]{StringUtils.EMPTY}, new Throwable("socket Object is null"));
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            SystemLogger.error("Occur error(UnknownHostException) when create Socket Connection, e,message= {0}", new String[]{e.getMessage()}, e);
            throw new RuntimeException(e);
        } catch(BindException e){
//            本地端口或对方server端口绑定失败关闭系统
            SystemLogger.error("Occur error(BindException) when create Socket,Fail to bind port or host,e.message={0}",new String[]{e.getMessage()},e);
            SystemLogger.error("Fail to Bind port , close System");
            CommandUtils.runAbnormalShellWithFunc(ExceptionEnum.BIND_EXCEPTION.getMsg(), s -> {
                SystemLogger.error("Abnormal shell fail to run,error msg : {0}",s);
                return null;
            });
            ExitSystemUtil.exitSystem(ExceptionEnum.BIND_EXCEPTION, ExceptionEnum.BIND_EXCEPTION.getMsg());

        } catch (ConnectException e){
            SystemLogger.error("Occur error(ConnectException) when create Socket,Fail to bind port or host,e.message={0}",new String[]{e.getMessage()},e);
            SystemLogger.error("Fail to connect to UMPS , close System");
            if (!GatewayConstant.SERVER_FACTORY.getConnection()
                    .isOpen() && GatewayConstant.SERVER_OPEN_CONNECT_TIMES.get() == 0) {
                ThreadUtil.sleep(5000);
            }
            if(!GatewayConstant.SERVER_FACTORY.getConnection().isOpen()){
                SystemLogger.error("Union Gateway Server Connect is close,client miss connect exception",new String[]{e.getMessage()},e);
                CommandUtils.runAbnormalShellWithFunc(ExceptionEnum.SOCKET_CONNECT_EXCEPTION.getMsg(), s -> {
                    SystemLogger.error("Abnormal shell fail to run,error msg : {0}",s);
                    return null;
                });
                ExitSystemUtil.exitSystem(ExceptionEnum.SOCKET_CONNECT_EXCEPTION, ExceptionEnum.SOCKET_CONNECT_EXCEPTION.getMsg());
            }
        }catch(SocketException e){
            SystemLogger.error("Occur error(SocketException) when create Socket Connection, e,message= {0}", new String[]{e.getMessage()}, e);
            if (!GatewayConstant.SERVER_FACTORY.getConnection()
                    .isOpen() && GatewayConstant.SERVER_OPEN_CONNECT_TIMES.get() == 0) {
                ThreadUtil.sleep(5000);
            }
            if(!GatewayConstant.SERVER_FACTORY.getConnection().isOpen()){
                SystemLogger.error("Union Gateway Server Connect is close,client miss socket exception",new String[]{e.getMessage()},e);
                CommandUtils.runAbnormalShellWithFunc(ExceptionEnum.SOCKET_EXCEPTION.getMsg(), s -> {
                    SystemLogger.error("Abnormal shell fail to run,error msg : {0}",s);
                    return null;
                });
                ExitSystemUtil.exitSystem(ExceptionEnum.SOCKET_EXCEPTION, ExceptionEnum.SOCKET_EXCEPTION.getMsg());
            }
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
                        SystemLogger.error("Creat TcpNetConnection failure,Prepare to close system");
                        CommandUtils.runAbnormalShellWithFunc(ExceptionEnum.CREAT_TCP_CONNECTION_EXCEPTION.getMsg(), s -> {
                            SystemLogger.error("Abnormal shell fail to run,error msg : {0}",s);
                            return null;
                        });
                        ExitSystemUtil.exitSystem(ExceptionEnum.CREAT_TCP_CONNECTION_EXCEPTION,"Socket params is Empty");
                        return null;
                    });

        }
    }



    private void settingsSocket(Socket sourceSocket, Socket destSocket) throws SocketException {
        if (sourceSocket.getSoTimeout() >= 0) {
            destSocket.setSoTimeout(sourceSocket.getSoTimeout());
        }
        if (sourceSocket.getSendBufferSize() > 0) {
            destSocket.setSendBufferSize(sourceSocket.getSendBufferSize());
        }
        if(sourceSocket.getReceiveBufferSize() > 0) {
            destSocket.setReceiveBufferSize(sourceSocket.getReceiveBufferSize());
        }
        destSocket.setKeepAlive(sourceSocket.getKeepAlive());
        destSocket.setTcpNoDelay(sourceSocket.getTcpNoDelay());
        if(sourceSocket.getSoLinger() >= 0){
            destSocket.setSoLinger(true, sourceSocket.getSoLinger());
        }
        if(sourceSocket.getTrafficClass() >= 0){
            destSocket.setTrafficClass(sourceSocket.getTrafficClass());
        }


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
            // It shouldn't be possible for the wrapped stream to change but, just in case...
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
