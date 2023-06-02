package com.iaspec.uniongatewayserver.unionHandle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import com.iaspec.uniongatewayserver.service.CpicService;
import com.iaspec.uniongatewayserver.util.CpicUtil;
import com.iaspec.uniongatewayserver.util.RecordUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.messaging.Message;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  17:16
 */
public class UnionServerMessageHandle {



    @Resource
    private CpicService cpicService;


    ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("SendMsg-task-%d")
            .build();

    ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2,
            4,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            threadFactory,
            new ThreadPoolExecutor.CallerRunsPolicy());

//    ms
//    队列最后的减去最先的时间 间隔 ms
    public static final int MORE_IDLE_MSG_TIME_LIMIT = 1200;

//    于上一次处理空消息逻辑的间隔时间
    public static final int HANDLE_TIMER = 5000;

//    队列长度
    private static final int IDLE_MAX_SIZE = 100;

    //    最后一次处理大量空消息的时间
    private static long LAST_HANDLE_TIME = System.currentTimeMillis();

//    存储空消息的时间戳
    public static ArrayBlockingQueue<Long> idleMsgTimeQueue = new ArrayBlockingQueue<>(IDLE_MAX_SIZE);


    public void handleServerMessage(Message<byte[]> message) throws Throwable{
        SystemLogger.debugMethod(getClass(), "handleServerMessage", (String) message.getHeaders()
                .get(IpHeaders.CONNECTION_ID), new String[]{"message.Headers"}, message.getHeaders());
        try {
            byte[] data = message.getPayload();
            String systemDestName = GatewayConstant.SYSTEM_DEST_NAME;
            RecordUtil.umps2GatewayRecord();
            String str;
            if (handleIdleMsg(data)){
                return;
            }
            if (GatewayConstant.IS_EBC_OR_ASCII) {
                str = new String(CpicUtil.convertToAsc(data, data.length), StandardCharsets.US_ASCII);
            } else {
                str = new String(data, StandardCharsets.US_ASCII);
            }
            String date = DateTime.now()
                    .toString();
            SystemLogger.info("Thread Id: {0}, Date : {1}, Received msg from UMPS", Thread.currentThread()
                    .getName(), date);
            SystemLogger.trace("Thread Id: {" + "0}, Date : {1}, content: {2}", Thread.currentThread()
                    .getName(), date, StringUtils.substring(str, 0, 20));


//            为了测试暂时注释，后续要打开
            cpicService.sendMessage2MainFrame(systemDestName,data);
//            cpicService.sendMsgBack(str);


        }  finally {
            SystemLogger.debugMethod(getClass(), "handleServerMessage", false, new String[]{"message.Headers"}, message.getHeaders());
        }

    }

    public boolean handleIdleMsg(byte[] data) throws InterruptedException {
        boolean isEmpty = data.length == 0;
        if (isEmpty){
            long current = System.currentTimeMillis();
            boolean isAdd = idleMsgTimeQueue.offer(current);
//            短时间很多空消息
            boolean isMoreIDLEMsg = false;
            if (!isAdd){
                if (idleMsgTimeQueue.size() == IDLE_MAX_SIZE && current - idleMsgTimeQueue.peek() < MORE_IDLE_MSG_TIME_LIMIT) {
                    isMoreIDLEMsg = true;
                }
                idleMsgTimeQueue.poll();
                idleMsgTimeQueue.offer(current);
            }
            if (isMoreIDLEMsg && current - LAST_HANDLE_TIME > HANDLE_TIMER){
                LAST_HANDLE_TIME = current;
                SystemLogger.error("Traffic spikes for incoming Empty messages");
                String executorName = Thread.currentThread().getName();
                if(StringUtils.startsWith(executorName,GatewayConstant.ClIENT_EXECUTOR_NAME)){
                    SystemLogger.error("Client executor accept more empty msg in show time");
                    GatewayConstant.CLIENT_FACTORY.getConnection().close();
                } else if (StringUtils.startsWith(executorName, GatewayConstant.SERVER_EXECUTOR_NAME)) {
                    SystemLogger.error("Server executor is crazy");
                }else {
                    SystemLogger.error("Main executor is crazy");
                }
            }
        }
        return isEmpty;
    }

}
