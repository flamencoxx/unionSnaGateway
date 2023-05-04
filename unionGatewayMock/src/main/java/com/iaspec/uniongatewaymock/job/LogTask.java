package com.iaspec.uniongatewaymock.job;


import com.iaspec.uniongatewaymock.model.TimeNewCons;
import com.iaspec.uniongatewaymock.model.TimerResultDTO;
import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

/**
 * @author Flamenco.xxx
 * @date 2022/10/28  14:50
 */
@Component
public class LogTask {

    @Value("${logCron}")
    private String logCron = "*/10 * * * * ?";

    @Scheduled(cron = "${logCron}")
    public void logTask() {
        TimeNewCons timeCons = TimeNewCons.getInstance();
        TimerResultDTO timerResultDTO = new TimerResultDTO();
        timerResultDTO.setSendTimes(String.valueOf(timeCons.sendTimes.get()));
        timerResultDTO.setSuccessTimes(String.valueOf(timeCons.successTimes.get()));
        timerResultDTO.setFailTimes(String.valueOf(timeCons.errorTimes.get()));
        timerResultDTO.setCount0And300(getCountNew(0,300));
        timerResultDTO.setCount300And400(getCountNew(300,400));
        timerResultDTO.setCount400And500(getCountNew(400,500));
        timerResultDTO.setCount800And9999(getCountNew(800,9999));
        timerResultDTO.setCount400And500(getCountNew(400,500));
        timerResultDTO.setCount500And600(getCountNew(500,600));
        timerResultDTO.setCount100And9999(getCountNew(100,9999));
        timerResultDTO.setCount200And300(getCountNew(200,300));
        timerResultDTO.setCount300And400(getCountNew(300,400));
        timerResultDTO.setCount400And1000(getCountNew(400,1000));
        timerResultDTO.setCount1000And5000(getCountNew(1000,5000));
        timerResultDTO.setCount5000(getCountNew(5000,Integer.MAX_VALUE));
        timerResultDTO.setCountLessThan0(getCountNew(-2,-1));
        timerResultDTO.setCountAllSend(String.valueOf(timeCons.sendCount.get()));
        OptionalDouble avg = timeCons.timekeepingList
                .stream()
                .filter(k -> k > 0L)
                .mapToDouble(Number::doubleValue)
                .average();
        timerResultDTO.setAvgTimer(String.valueOf(avg.orElse(-1)));
        SystemLogger.info("Performance record info,SendTimes={0},SuccessTimes={1},FailTimes={2},0-100ms={3},100-300ms={4},400-500ms={5},500-600ms={6},700-800ms={7},800-9999ms={8},avg={9},CountAllSend={10}",
                timerResultDTO.getSendTimes(),
                timerResultDTO.getSuccessTimes(),
                timerResultDTO.getFailTimes(),
                timerResultDTO.getCount0And300(),
                timerResultDTO.getCount300And400(),
                timerResultDTO.getCount400And500(),
                timerResultDTO.getCount500And600(),
                timerResultDTO.getCount700And800(),
                timerResultDTO.getCount800And9999(),
                timerResultDTO.getAvgTimer(),timerResultDTO.getCountAllSend());
    }
    public String getCountNew(int min,int max) {
        TimeNewCons timeCons = TimeNewCons.getInstance();
        long count = timeCons.timekeepingList
                .stream()
                .filter(k -> min <= k && k < max)
                .count();
        return String.valueOf(count);
    }
}
