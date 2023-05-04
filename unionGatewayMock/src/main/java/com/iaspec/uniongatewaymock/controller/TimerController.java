package com.iaspec.uniongatewaymock.controller;



import com.iaspec.uniongatewaymock.model.TimeCons;
import com.iaspec.uniongatewaymock.model.TimeNewCons;
import com.iaspec.uniongatewaymock.model.TimerResultDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.OptionalDouble;

/**
 * @author Flamenco.xxx
 * @date 2022/9/22  10:57
 */

@Controller
public class TimerController {

    @RequestMapping(value = "/timer", method = RequestMethod.GET)
    public ResponseEntity<TimerResultDTO> getTimerNewInfo(){
        TimeNewCons timeCons = TimeNewCons.getInstance();
        TimerResultDTO timerResultDTO = new TimerResultDTO();
        timerResultDTO.setSendTimes(String.valueOf(timeCons.sendTimes.get()));
        timerResultDTO.setSuccessTimes(String.valueOf(timeCons.successTimes.get()));
        timerResultDTO.setFailTimes(String.valueOf(timeCons.errorTimes.get()));
        timerResultDTO.setCount0And100(getCountNew(0,100));
        timerResultDTO.setCount100And200(getCountNew(100,200));
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

        return new ResponseEntity<>(timerResultDTO, HttpStatus.OK);
    }

    public String getCountNew(int min,int max) {
        TimeNewCons timeCons = TimeNewCons.getInstance();
        long count = timeCons.timekeepingList
                .stream()
                .filter(k -> min < k && k <= max)
                .count();
        return String.valueOf(count);
    }

    @RequestMapping(value = "/timerOld", method = RequestMethod.GET)
    public ResponseEntity<TimerResultDTO> getTimerInfo(){
        TimeCons timeCons = TimeCons.getInstance();
        TimerResultDTO timerResultDTO = new TimerResultDTO();
        timerResultDTO.setSendTimes(String.valueOf(timeCons.sendTimes.get()));
        timerResultDTO.setSuccessTimes(String.valueOf(timeCons.successTimes.get()));
        timerResultDTO.setFailTimes(String.valueOf(timeCons.errorTimes.get()));
        timerResultDTO.setCount0And100(getCount(0,100));
        timerResultDTO.setCount100And200(getCount(100,200));
        timerResultDTO.setCount200And300(getCount(200,300));
        timerResultDTO.setCount300And400(getCount(300,400));
        timerResultDTO.setCount400And1000(getCount(400,1000));
        timerResultDTO.setCount1000And5000(getCount(1000,5000));
        timerResultDTO.setCount5000(getCount(5000,Integer.MAX_VALUE));
        timerResultDTO.setCountLessThan0(getCount(-2,-1));
        timerResultDTO.setCountAllSend(String.valueOf(timeCons.sendCount.get()));
        OptionalDouble avg = timeCons.timekeepingMap.values()
                .stream()
                .filter(k -> k > 0L)
                .mapToDouble(Number::doubleValue)
                .average();
        timerResultDTO.setAvgTimer(String.valueOf(avg.orElse(-1)));

        return new ResponseEntity<>(timerResultDTO, HttpStatus.OK);
    }

    public String getCount(int min,int max) {
        TimeCons timeCons = TimeCons.getInstance();
        long count = timeCons.timekeepingMap.values()
                .stream()
                .filter(k -> min < k && k <= max)
                .count();
        return String.valueOf(count);
    }
}
