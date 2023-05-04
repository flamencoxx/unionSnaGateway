package com.iaspec.uniongatewaymock.model;

import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iaspec.uniongatewaymock.constant.GatewayConstant;


import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Flamenco.xxx
 * @date 2022/10/21  9:27
 */
public class TimeNewCons {

    public AtomicInteger sendCount = new AtomicInteger(0);

    public boolean isTimerTest = false;

    private volatile static TimeNewCons timeCons;

    private final Holder<String> lastKey = new Holder<>();

    public final List<Long> timekeepingList = Lists.newCopyOnWriteArrayList();

    public final Map<String, Long> timekeepingMap = Maps.newConcurrentMap();

    public final AtomicLong sendTimes = new AtomicLong(0);

    public final AtomicLong successTimes = new AtomicLong(0);

    public final AtomicLong errorTimes = new AtomicLong(0);

    public final TimeInterval timer = new TimeInterval();

    private TimeNewCons() {
    }

    public static TimeNewCons getInstance() {
        if (timeCons == null) {
            synchronized (TimeNewCons.class) {
                if (timeCons == null) {
                    timeCons = new TimeNewCons();
                }
            }
        }
        return timeCons;
    }

    public void start() {
        sendTimes.incrementAndGet();
    }

    public long acceptMsg(String msg) {
        if (GatewayConstant.sendRecords.containsKey(msg)) {
            TimeInterval interval = GatewayConstant.sendRecords.get(msg);
            long timer = interval.interval();
            timekeepingList.add(timer);
            successTimes.incrementAndGet();
            GatewayConstant.sendRecords.remove(msg);
            return timer;
        }
        return -1;
    }


}
