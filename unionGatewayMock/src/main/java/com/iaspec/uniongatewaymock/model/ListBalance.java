package com.iaspec.uniongatewaymock.model;


import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Flamenco.xxx
 * @date 2022/8/19  9:05
 */
public class ListBalance<T> {


    private List<T> balanceList;

    public ListBalance(List<T> list) {
        this.balanceList = list;
    }

    public ListBalance(){
    }

    AtomicInteger index = new AtomicInteger(0);

    public synchronized T choice(List<T> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        if (index.get() == Integer.MAX_VALUE){
            index.set(0);
        }
        int sum = list.size();
        T t = list.get(index.get() % sum);
        index.incrementAndGet();
        return t;
    }


    public synchronized T choice(){
        if(CollectionUtils.isEmpty(this.balanceList)){
            return null;
        }
        if (index.get() == Integer.MAX_VALUE){
            index.set(0);
        }
        int sum = this.balanceList.size();
        T t = this.balanceList.get(index.get() % sum);
        index.incrementAndGet();
        return t;
    }
}
