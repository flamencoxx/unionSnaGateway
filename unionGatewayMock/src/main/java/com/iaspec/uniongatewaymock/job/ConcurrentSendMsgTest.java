package com.iaspec.uniongatewaymock.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.controller.SendTestController;
import com.iaspec.uniongatewaymock.model.ListBalance;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Flamenco.xxx
 * @date 2022/9/22  14:44
 */

@Component
public class ConcurrentSendMsgTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${concurrentNum}")
    private int concurrentNum = 20;

    @Value("${msgContent}")
    public String msgContent = StringUtils.EMPTY;

    @Value("${concurrentSendTimes}")
    public int sendTimes = 0;

    public AtomicInteger aSendTimes = new AtomicInteger(0);


    ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("flamenco-pool-%d")
            .build();

    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 6, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), factory, new ThreadPoolExecutor.CallerRunsPolicy());

    @Autowired
    private SendTestController sendTestController;

    private final ListBalance<Integer> balance = new ListBalance<>(ImmutableList.of(1, 2));


    private ReentrantLock lock = new ReentrantLock();


    @PostConstruct
    public void init() {
//        realCron.set(content);
    }

    @Scheduled(cron = "${concurrentCron}")
    private void concurrentSendMsgTest() {

        try {

            for (int i = 0; i < concurrentNum; i++) {
                aSendTimes.incrementAndGet();
                if (aSendTimes.get() > sendTimes){
                    return;
                }
                poolExecutor.execute(new SendMsgJob());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }


    }

    class SendMsgJob implements Runnable {
        @Override
        public void run() {

            try {
//                lock.lock();
                String id = IdUtil.getSnowflakeNextIdStr();
                String content = randomWord();
                if (StringUtils.isNotBlank(msgContent)) {
                    content = msgContent;
                }
                String date = DateUtil.now();
                if (StringUtils.isEmpty(GatewayConstant.CONNECTION_ID)) {
                    sendTestController.sendMsg(formatClientMsg(content));
                } else {
                    if (balance.choice() == 1) {
                        sendTestController.sendMsg(formatClientMsg(content));
                    } else {
                        sendTestController.sendMsg2Client(formatServerMsg(content));
                    }

                }

//                sendTestController.sendMsg(formatClientMsg(content));
//                SystemLogger.info("CurrentSendMsgTest, date= {0}, msgId={1}, msgContent={2}", date, id, formatClientMsg(content));
            } finally {

//                lock.unlock();
            }

        }
    }

    class SingleSendMsgJob implements Runnable {
        MessageChannel clientOutputChannel = applicationContext.getBean("unionMockClientOutChannel", MessageChannel.class);

        @SneakyThrows
        @Override
        public void run() {
            try {
                String content = converContent(randomWord());
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                InetAddress addr = InetAddress.getLocalHost();
//                lock.lock();
                Message<byte[]> message = MessageBuilder.withPayload(bytes)
                        .setHeader(IpHeaders.IP_ADDRESS, addr)
                        .build();


                boolean isSend = clientOutputChannel.send(message);
                Console.log(isSend + " msg: " + content);
            } finally {
//                lock.unlock();

            }
        }
    }

    public String converContent(String word) {
        int len = word.length();
        String res = String.valueOf(len);
        String result = StringUtils.leftPad(res, 4, "0");
        return result + word;
    }

    public String randomWord() {
//        return "#" + "helloWorld1";
        return RandomStringUtils.randomAscii(50, 60);
    }

    public String formatClientMsg(String msg) {
        String str = "##" + GatewayConstant.mockClientPort + "/" + GatewayConstant.seqNo.incrementAndGet() + "/" + msg;
        return str;
    }

    public String formatServerMsg(String msg) {
        String str = "##" + GatewayConstant.mockServerPort + "/" + GatewayConstant.seqNo.incrementAndGet() + "/" + msg;
        return str;
    }

}

