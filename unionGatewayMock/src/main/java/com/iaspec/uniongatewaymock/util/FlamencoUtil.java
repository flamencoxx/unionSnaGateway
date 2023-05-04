package com.iaspec.uniongatewaymock.util;

/**
 * @author Flamenco.xxx
 * @date 2022/10/20  17:44
 */
public class FlamencoUtil {

    public static String convert2Accept(String content) {

//        StringUtils.replaceOnce(str, "##", "**");
        return content.replaceFirst("##", "**");
    }
}
