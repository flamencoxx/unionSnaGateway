package com.iaspec.uniongatewaymock.model;

import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Maps;


import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Flamenco.xxx
 * @date 2022/9/21  18:03
 */
public class TimeCons {

    public AtomicInteger sendCount = new AtomicInteger(0);

    public boolean isTimerTest = false;

    private volatile static TimeCons timeCons;

    private final Holder<String> lastKey = new Holder<>();

    public final Map<String, Long> timekeepingMap = Maps.newConcurrentMap();

    public final AtomicLong sendTimes = new AtomicLong(0);

    public final AtomicLong successTimes = new AtomicLong(0);

    public final AtomicLong errorTimes = new AtomicLong(0);

    public  final TimeInterval timer = new TimeInterval();

    private TimeCons() {
    }

    public static TimeCons getInstance() {
        if (timeCons == null) {
            synchronized (TimeCons.class) {
                if (timeCons == null) {
                    timeCons = new TimeCons();
                }
            }
        }
        return timeCons;
    }

    public void doTimerStart(){
        try {
            if (isTimerTest) {
                if (StringUtils.isNotBlank(this.lastKey.get()) && this.timekeepingMap.get(this.lastKey.get()) == -1L){
                    this.errorTimes.incrementAndGet();
                    this.timer.clear();
                    SystemLogger.info("Timer is error or timeout");
                }
                long times = this.sendTimes.incrementAndGet();
                this.timekeepingMap.put(String.valueOf(times), -1L);
                this.lastKey.set(String.valueOf(times));
                this.timer.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

    public void doTimerStop(){
        try {
            if (isTimerTest) {
                this.successTimes.incrementAndGet();
                long times = this.timer.interval();
                this.timekeepingMap.put(String.valueOf(this.sendTimes.get()), times);
                SystemLogger.info("accept Msg from gateway, sendKey=" + this.sendTimes.get() + ", Time consuming:" + times + " ms");
//                Console.log("lastKey={}, successTimes={}, errorTimes={}" , this.lastKey.get(),this.successTimes.get(),this.errorTimes.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.timer.clear();
            isTimerTest = false;
        }
    }
}