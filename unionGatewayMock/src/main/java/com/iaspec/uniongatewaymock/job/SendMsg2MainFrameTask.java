package com.iaspec.uniongatewaymock.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.ImmutableList;


import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import com.iaspec.uniongatewaymock.controller.SendTestController;
import com.iaspec.uniongatewaymock.model.Holder;
import com.iaspec.uniongatewaymock.model.ListBalance;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Flamenco.xxx
 * @date 2022/9/21  15:56
 */

@Component
public class SendMsg2MainFrameTask {

    @Autowired
    private SendTestController sendTestController;

    @Value("${jobCron}")
    private String jobCron = "*/5 * * * * ?";

    @Value("${isDuplex}")
    private Boolean isDuplex = false;

    @Value("${sendTimes}")
    public int sendTimes = 0;

    public AtomicInteger aSendTimes = new AtomicInteger(0);

    @Value("${msgContent}")
    public String msgContent = StringUtils.EMPTY;

    private final Holder<String> realCron = new Holder<>();

    private final ListBalance<Integer> balance = new ListBalance<>(ImmutableList.of(1, 2));




//    @Value("${content}")
//    private String content = "0004#123";

    @PostConstruct
    public void init() {
//        realCron.set(content);
    }

    @Scheduled(cron = "${jobCron}")
    private void sendMsg2MainFrameTask() {
        String id = IdUtil.getSnowflakeNextIdStr();
        String content = randomWord();
        if(StringUtils.isNotBlank(msgContent)){
            content = msgContent;
        }
        aSendTimes.incrementAndGet();
        if(aSendTimes.get() > sendTimes){
            return;
        }
        if (isDuplex){
            if(StringUtils.isEmpty(GatewayConstant.mockServerConnectionId)){
                sendTestController.sendMsg(formatClientMsg(content));
            }else {
                if (balance.choice() == 1){
                    sendTestController.sendMsg(formatClientMsg(content));
                }else {
                    sendTestController.sendMsg2Client(formatServerMsg(content));
                }

            }
        } else {
            sendTestController.sendMsg(formatClientMsg(content));
        }



        String date = DateUtil.now();
//        SystemLogger.info("线程ID: {0}, MsgId : {1}, date : {2}, MsgContent : {3}",Thread.currentThread().getName(),id,date,formatServerMsg(content));

    }

    public String converContent(String word){
        int len = word.length();
        String res = String.valueOf(len);
        String result = StringUtils.leftPad(res,4,"0");
        return result + word;
    }

    public String randomWord() {
        return RandomStringUtils.randomAscii(50, 100);
    }

    public String formatClientMsg(String msg) {
        String str = "##" + GatewayConstant.mockClientPort + "/" + GatewayConstant.seqNo.incrementAndGet() + "/" + msg;
        return str;
    }

    public String formatServerMsg(String msg) {
        String str = "##" + GatewayConstant.mockServerPort +"/" + GatewayConstant.seqNo.incrementAndGet() +"/" + msg;
        return str;
    }

}
