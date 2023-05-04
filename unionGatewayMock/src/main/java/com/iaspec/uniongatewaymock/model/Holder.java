package com.iaspec.uniongatewaymock.model;

/**
 * @author Flamenco.xxx
 * @date 2022/9/21  17:08
 */
public class Holder<T> {

    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;

    }
}
