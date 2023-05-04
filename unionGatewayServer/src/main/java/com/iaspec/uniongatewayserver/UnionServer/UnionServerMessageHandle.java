package com.iaspec.uniongatewayserver.UnionServer;

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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  17:16
 */
public class UnionServerMessageHandle {

    @Autowired
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


    public void handleServerMessage(Message<byte[]> message) throws Throwable{
        SystemLogger.debugMethod(getClass(), "handleServerMessage", (String) message.getHeaders()
                .get(IpHeaders.CONNECTION_ID), new String[]{"message.Headers"}, message.getHeaders());

        try {
            SystemLogger.debug("message.payload.length={0}", message.getPayload().length);

            byte[] data = message.getPayload();
//            获取轮训类,通过choice方法不断轮训下一个
            String systemDestName = GatewayConstant.SYSTEM_DEST_NAME;
//            send Msg to mainFrame

            RecordUtil.umps2GatewayRecord();

            String str;
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
//            cpicService.sendMessage2MainFrame(systemDestName,data);
            cpicService.sendMsgBack(str);

//            throw new Exception("test");
        }  finally {
            SystemLogger.debugMethod(getClass(), "handleServerMessage", false, new String[]{"message.Headers"}, message.getHeaders());
        }

    }

}
