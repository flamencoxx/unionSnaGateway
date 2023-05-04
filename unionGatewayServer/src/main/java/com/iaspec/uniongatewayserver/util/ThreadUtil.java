package com.iaspec.uniongatewayserver.util;

/**
 * @author Flamenco.xxx
 * @date 2022/9/9  9:47
 */
public class ThreadUtil {


    public static boolean sleep(Number millis) {
        if (millis == null) {
            return true;
        }
        return sleep(millis.longValue());
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     * @since 5.3.2
     */
    public static boolean sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

}
