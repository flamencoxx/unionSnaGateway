package com.iaspec.uniongatewayserver.service.impl;

import COM.ibm.eNetwork.cpic.*;
import com.google.common.collect.Lists;
import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.exception.ServiceException;
import com.iaspec.uniongatewayserver.model.AcceptResult;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.service.CpicService;
import com.iaspec.uniongatewayserver.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpConnectionSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author Flamenco.xxx
 * @date 2022/8/19  9:10
 */


public class CpicServiceImpl implements CpicService {

    private static final int DATA_BUFFER_SIZE = 4096;

    public static boolean lastTimeIsAccept = false;

    private static int CM_CID_SIZE = 200;

    private static ReentrantLock lock = new ReentrantLock();


    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void sendMessage2MainFrame(String systemDestName, byte[] data) {

        SystemLogger.debugMethod(getClass(), "sendMessage2MainFrame", true, new String[]{"systemDestName"}, systemDestName);

        try {
            //        CPIC init
            CPIC cpic = new CPIC();
            CPICReturnCode cpicReturnCode = new CPICReturnCode(CPICReturnCode.CM_OK);
            CPICControlInformationReceived rtsReceived = new CPICControlInformationReceived(CPICControlInformationReceived.CM_NO_CONTROL_INFO_RECEIVED);
            byte[] conversationId = new byte[CPIC.CM_CID_SIZE];
            cpic.cminit(conversationId, systemDestName, cpicReturnCode);
            SystemLogger.info("CPIC init result: conversationId= {0}, systemDestName= {1}, CPICReturnCode= {2}", conversationId, systemDestName, cpicReturnCode.intValue());
            checkReturnCode(cpicReturnCode);

            if (!GatewayConstant.IS_EBC_OR_ASCII) {
                data = CpicUtil.convertToEbc(data, data.length);
            }
//        CPIC ALLOCATE
            cpic.cmallc(conversationId, cpicReturnCode);
            SystemLogger.info("CPIC allocate result: conversationId= {0}, systemDestName= {1}, CPICReturnCode= {2}", conversationId, systemDestName, cpicReturnCode.intValue());

            checkReturnCode(cpicReturnCode);
            CPICLength dataLength = new CPICLength(data.length);


//        CPIC SEND DATA
            cpic.cmsend(conversationId, data, dataLength, rtsReceived, cpicReturnCode);

            SystemLogger.info("CPIC send data message: conversationId= {0}, systemDestName= {1}, CPICReturnCode= {2}, cpicControlInformationReceived= {3}", conversationId, systemDestName, cpicReturnCode.intValue(), rtsReceived.intValue());

            checkReturnCode(cpicReturnCode);
//        deallocate
//        close connection
            cpic.cmdeal(conversationId, cpicReturnCode);
        } catch (ServiceException e) {
            SystemLogger.error("Occurs error while send message, return code is not success, e.message = {0}", new String[]{e.getMessage()}, e);
//            throw new RuntimeException(e);
        } catch (Exception e) {
            SystemLogger.error("Occurs error while send message, e.message = {}", new String[]{e.getMessage()}, e);
        } finally {
            SystemLogger.debugMethod(getClass(), "sendMessage2MainFrame", false, new String[]{"systemDestName"}, systemDestName);
        }
    }

    public String converContent(String word) {
        int len = word.length();
        String res = String.valueOf(len);
        String result = StringUtils.leftPad(res, 4, "0");
        return result + word;
    }

    @Override
    public void acceptMessageFromMainFrame() {
        CPICReturnCode cpicReturnCode = new CPICReturnCode(CPICReturnCode.CM_OK);
        CPIC cpic = new CPIC();
        byte[] conversationId = new byte[CPIC.CM_CID_SIZE];
        CPICDataReceivedType dataReceivedType = new CPICDataReceivedType(0);
        CPICLength requestLength = new CPICLength(GatewayConstant.REQUEST_LEN);
        CPICLength receivedLength = new CPICLength(0);
        CPICStatusReceived statusReceived = new CPICStatusReceived(0);
        CPICControlInformationReceived rtsReceived = new CPICControlInformationReceived(CPICControlInformationReceived.CM_NO_CONTROL_INFO_RECEIVED);
        Thread acceptThread = new Thread(() -> {
            SystemLogger.info("Start accepting data from mainframe continuously");
            while (true) {
                byte[] dataBuffer = new byte[DATA_BUFFER_SIZE];
                do {
                    cpic.cmaccp(conversationId, cpicReturnCode);
                    long sleepTime = GatewayConstant.TIME_INTERVAL_LONG;
                    if (isLastTimeIsAccept()) {
                        sleepTime = GatewayConstant.TIME_INTERVAL_SHORT;
                        setLastTimeIsAccept(false);
                    }
                    setLastTimeIsAccept(GatewayConstant.whiteListCode.contains(cpicReturnCode.intValue()));
                    ThreadUtil.sleep(sleepTime);
                } while (!GatewayConstant.whiteListCode.contains(cpicReturnCode.intValue()));
                SystemLogger.info("Prepare the data returned by SNA, conversationId= {0}, cpicReturnCode= {1}", new String(conversationId), cpicReturnCode);

                try {
//                    下面操作可以改成异步处理
                    cpic.cmrcv(conversationId, dataBuffer, requestLength, dataReceivedType, receivedLength, statusReceived, rtsReceived, cpicReturnCode);

//                    SystemLogger.info("receivedLen={0},requestLen={1}", receivedLength.intValue(), requestLength.intValue());
                    checkReturnCode(cpicReturnCode);
                    SystemLogger.info("buffer len is  {0}", getValidLength(dataBuffer));
                    byte[] result = new byte[receivedLength.intValue()];
                    System.arraycopy(dataBuffer, 0, result, 0, receivedLength.intValue());
                    if (!GatewayConstant.IS_EBC_OR_ASCII) {
                        result = CpicUtil.convertToAsc(result, result.length);
                    }
                    SystemLogger.info("Receive data returned from SNA, conversationId = {0}, requestLength = {1}, dataReceivedType = {2}, receivedLength = {3}, statusReceived = {4}, rtsReceived = {5}, cpicReturnCode = {6}, msgByteLen = {8}", new String(conversationId), requestLength.intValue(), dataReceivedType.toString(), receivedLength.intValue(), statusReceived.intValue(), rtsReceived.intValue(), cpicReturnCode.intValue(), getValidLength(result));
                    String receivedStr = new String(CpicUtil.convertToAsc(result, result.length), StandardCharsets.US_ASCII);
                    SystemLogger.trace("Receive data returned from SNA, conversationId = {0}, content = {1}",new String(conversationId),StringUtils.substring(receivedStr,0,20));
                    AcceptResult acceptResult = AcceptResult.builder()
                            .conversationId(new String(conversationId))
                            .data(result)
                            .requestLength(requestLength)
                            .dataReceivedType(dataReceivedType)
                            .receivedLength(receivedLength)
                            .statusReceived(statusReceived)
                            .rtsReceived(rtsReceived)
                            .cpicReturnCode(cpicReturnCode)
                            .build();

                    this.sendMsg2Union(acceptResult);

                } catch (ServiceException e) {
                    SystemLogger.error("Occurs error while accepting data,return code is not success, e,message= {0}", new String[]{e.getMessage()}, e);
                } catch (Exception e) {
                    SystemLogger.error("Occurs error while accepting data, e.message = {0}", new String[]{e.getMessage()}, e);
//                    throw new RuntimeException(e);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
        acceptThread.setName("Accept-Host-Msg-Thread");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public int getValidLength(byte[] bytes) {
        int i = 0;
        if (null == bytes || 0 == bytes.length) return i;
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0') break;
        }
        return i + 1;
    }

    public void sendMsg2Union(AcceptResult acceptResult) throws Throwable {
        SystemLogger.debugMethod(getClass(), "sendMsg2Union", true, new String[]{""});
        try {
//            判断返回的data和returnCode，处理AcceptResult,后续需要修改

            if (!GatewayConstant.whiteListCode.contains(acceptResult.getCpicReturnCode()
                    .intValue())) {
                throw new ServiceException("500", "return code is not right", ServiceException.LogLevel.ERROR);
            }

            boolean isSend;
            if(GatewayConstant.isDuplex.get()){
                isSend = server2UMPS(acceptResult.getData());
//                isSend = clientSend2UMPS(acceptResult.getData(), GatewayConstant.CLIENT_OUTBOUND_CHANNEL);
//                isSend = GatewayConstant.SEND_FUNC.choice().apply(acceptResult.getData());
            }else {
                isSend = clientSend2UMPS(acceptResult.getData(), GatewayConstant.CLIENT_OUTBOUND_CHANNEL);
            }

            if (isSend){
                RecordUtil.Gateway2UmpsRecord();
            }

        } finally {
            SystemLogger.debugMethod(getClass(), "sendMsg2Union", false, new String[]{""});
        }
    }

    @Override
    public void autoSendMsgback() throws Throwable{
        Random random = new Random();
//        int randomSleep = random.nextInt(500) + 20;
//        ThreadUtil.sleep(randomSleep);
        AcceptResult acceptResult = new AcceptResult();
        acceptResult.setCpicReturnCode(new CPICReturnCode(0));
        String testContent = "autoSendMsgBack";
        byte[] data = testContent.getBytes(StandardCharsets.US_ASCII);
        acceptResult.setData(data);

        this.sendMsg2Union(acceptResult);
    }

    @Override
    public void sendMsgBack(String content) throws Throwable {
        AcceptResult acceptResult = new AcceptResult();
        acceptResult.setCpicReturnCode(new CPICReturnCode(0));
        byte[] data = convertMsg(content).getBytes(StandardCharsets.US_ASCII);
        if (GatewayConstant.IS_EBC_OR_ASCII) {
            acceptResult.setData(CpicUtil.convertToEbc(data, data.length));
        } else {
            acceptResult.setData(data);
        }
        SystemLogger.info("send Msg back ,msg = {0}", new String(data, StandardCharsets.US_ASCII));
        this.sendMsg2Union(acceptResult);
    }

    public String convertMsg(String content) {
        return content.replaceFirst("##", "**");
    }

    public static boolean serverSend2UMPS(byte[] data, MessageChannel channel, String connectionId) throws Exception {
        Message<byte[]> message = MessageBuilder.withPayload(data)
                .setHeader(IpHeaders.CONNECTION_ID, connectionId)
                .build();
        return channel.send(message);
    }

    public static boolean clientSend2UMPS(byte[] data, MessageChannel channel) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        Message<byte[]> message = MessageBuilder.withPayload(data)
                .setHeader(IpHeaders.IP_ADDRESS, addr)
                .build();
        return channel.send(message);
    }

    public static boolean server2UMPS(byte[] data){
        Message<byte[]> message = MessageBuilder.withPayload(data)
                .setHeader(IpHeaders.CONNECTION_ID, GatewayConstant.IP_CONNECTION_ID.get())
                .build();
        GatewayConstant.SERVER_OUTBOUND_CHANNEL.send(message);
        return true;
    }

    public static boolean client2UMPS(byte[] data){
        InetAddress addr;
        Message<byte[]> message = null;
        try {
            addr = InetAddress.getLocalHost();
            message = MessageBuilder.withPayload(data)
                    .setHeader(IpHeaders.IP_ADDRESS, addr)
                    .build();
        } catch (Throwable e) {
            SystemLogger.error("Occur a error when client send msg to UMPS");
        }
        if (message == null){
            return false;
        }
        return GatewayConstant.CLIENT_OUTBOUND_CHANNEL.send(message);
    }



    @Override
    public void checkReturnCode(CPICReturnCode returnCode) throws ServiceException {
        int code = returnCode.intValue();
        boolean lockResult = false;
        if (GatewayConstant.fatalErrorSet.contains(code)) {
            try {
                lockResult = lock.tryLock(10, TimeUnit.SECONDS);
                if (lockResult) {
                    SystemLogger.error("Host return fatal error code,System close");
                    CommandUtils.runAbnormalShell("Capture a fatal error,returnCode = " + code);
                    SystemLogger.error("Abnormal shell finish,system prepare close");
                    ExitSystemUtil.exitSystem(ExceptionEnum.FATAL_ERROR,"check a fatal error,Got the lock");
                } else {
                    SystemLogger.error("System exception,maybe not send email");
                    ExitSystemUtil.exitSystem(ExceptionEnum.FATAL_ERROR,"check a fatal error,Can't get the lock");
                }

            } catch (Throwable e) {
                SystemLogger.error("fail to run shell script msg,", new String[]{}, e);
                ExitSystemUtil.exitSystem(ExceptionEnum.FATAL_ERROR,"check a fatal error,exit miss Exception");
            } finally {
                if (lockResult) {
                    lock.unlock();
                }
            }
        }
        if (!GatewayConstant.whiteListCode.contains(code)) {
            int errorCount = GatewayConstant.RETURN_CODE_ERROR_COUNT.incrementAndGet();
            SystemLogger.error("Occurs error while checking return code, return code is {0}, error times is {1}", new String[]{String.valueOf(code), String.valueOf(errorCount)}, new Throwable("return code is not success"));
            if (errorCount > GatewayConstant.ERROR_COUNT_LIMIT) {
                try {
                    lockResult = lock.tryLock(10, TimeUnit.SECONDS);
                    if (lockResult) {
                        SystemLogger.error("Return code error count have over " + GatewayConstant.ERROR_COUNT_LIMIT + " times,shutdown system", new String[]{""}, new Throwable("error count over" + GatewayConstant.ERROR_COUNT_LIMIT + "times"));
                        CommandUtils.runAbnormalShell("Capture error code over limit");
                        SystemLogger.error("Abnormal shell finish,system prepare close");
                        ExitSystemUtil.exitSystem(ExceptionEnum.CONTINUOUS_EXCEPTION_RETURN_CODE,"Continuous check for exception return codes,get lock");
                    } else {
                        SystemLogger.error("System exception,maybe not send email");
                        ExitSystemUtil.exitSystem(ExceptionEnum.CONTINUOUS_EXCEPTION_RETURN_CODE,"Continuous check for exception return codes,Can't get lock");
                    }
                } catch (Throwable e) {
                    SystemLogger.error("Maybe fail to run shell script msg", new String[]{}, e);
                    ExitSystemUtil.exitSystem(ExceptionEnum.CONTINUOUS_EXCEPTION_RETURN_CODE,"Continuous check for exception return codes,exit miss a exception");
                } finally {
                    if (lockResult) {
                        lock.unlock();
                    }
                }
            }
            throw new ServiceException("500", "return code exception,return code : " + code, ServiceException.LogLevel.ERROR);
        } else {
            GatewayConstant.RETURN_CODE_ERROR_COUNT.set(0);
        }

    }


    public void init() throws InterruptedException {

    }


    public static synchronized boolean isLastTimeIsAccept() {
        return lastTimeIsAccept;
    }

    public static synchronized void setLastTimeIsAccept(boolean lastTimeIsAccept) {
        CpicServiceImpl.lastTimeIsAccept = lastTimeIsAccept;
    }


    public static void exitSystem() {
        try {
            Thread exitThread = new Thread(() -> {
                SystemLogger.info("Call JVM shutdown hook method");
                System.exit(0);
            });
            exitThread.setName("exit-thread");
            exitThread.setDaemon(true);
            exitThread.start();
        } catch (Throwable e) {
            SystemLogger.error("System exit error", e);
        } finally {
            Thread shutdownThread = new Thread(() -> {
                try {
                    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                    String jvmName = bean.getName();
                    long pid = Long.parseLong(jvmName.split("@")[0]);
                    SystemLogger.info("PID  = " + pid);
                    Thread.sleep(30000);
                    SystemLogger.info("Call JVM shutdown hook method occur exception,Force System shutdown");
                } catch (Throwable e) {
                    SystemLogger.error("System shutdown Exception");
                } finally {
                    Runtime.getRuntime()
                            .halt(-1);
                }
            });
            shutdownThread.setName("shutdown-thread");
            shutdownThread.setDaemon(true);
            shutdownThread.start();
        }
    }

}
