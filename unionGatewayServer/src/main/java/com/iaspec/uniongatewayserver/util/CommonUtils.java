package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.ws.Holder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    protected static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    protected static final String PATTERN_PHONE_NUMEBR = "^\\+[0-9]{1,3}-[0-9()+\\-]{1,30}$";
    protected static final String PATTERN_AMOUNT = "^\\d{1,15}(\\.\\d{1,2})?$";
    protected static final String PATTERN_BIC = "^[A-Z]{6,6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3,3}){0,1}$";
    protected static final String PATTERN_CLEARING_CODE = "^[A-Z0-9]{3,3}$";
    protected static final String PATTERN_C = "[A-z0-9 !@#$%^&*()_+~{}|:\"<>?`=\\[\\]\\\\;',./-]+";
    protected static final String PATTERN_U =
            // "[\\p{L}\\p{M}\\p{Z}\\p{S}\\p{N}\\p{P}\\p{C}&&[^\\p{Cc}]]+";
            "[\\P{Cc}]+";

    protected static final Pattern patternEmail = Pattern.compile(PATTERN_EMAIL);
    protected static final Pattern patternPhoneNumber = Pattern.compile(PATTERN_PHONE_NUMEBR);
    protected static final Pattern patternAmount = Pattern.compile(PATTERN_AMOUNT);
    protected static final Pattern patternBic = Pattern.compile(PATTERN_BIC);
    protected static final Pattern patternClearingCode = Pattern.compile(PATTERN_CLEARING_CODE);
    protected static final Pattern patternC = Pattern.compile(PATTERN_C);
    protected static final Pattern patternU = Pattern.compile(PATTERN_U, Pattern.UNICODE_CHARACTER_CLASS);

    public static String generateUuid() {
        return null;
    }

    public static Date convertMonthStart(Date startDate) {
        if (startDate == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        return c.getTime();
    }

    public static Date convertMonthEnd(Date endDate) {
        if (endDate == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59, 59);
        return c.getTime();
    }

    public static Date convertWeekStart(Date startDate) {
        if (startDate == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        return c.getTime();
    }

    public static Date convertWeekEnd(Date endDate) {
        if (endDate == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59, 59);
        return c.getTime();
    }

    public static Date convertStartDate(Date startDate) {
        if (startDate == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        return c.getTime();
    }

    public static boolean validateStringU(String str, int maxLength, boolean mandatory) {
        return validateString(str, patternU, maxLength, mandatory);
    }

    public static boolean validateStringC(String str, int maxLength, boolean mandatory) {
        return validateString(str, patternC, maxLength, mandatory);
    }

    public static boolean validateStringC(String str, int fixLength) {
        return validateString(str, patternC, fixLength);
    }

    protected static boolean validateString(String str, Pattern ptn, int fixLength) {
        if (StringUtils.isBlank(str)) return false;
        if (str.length() != fixLength) return false;
        return ptn.matcher(str)
                .matches();
    }

    protected static boolean validateString(String str, Pattern ptn, int maxLength, boolean mandatory) {
        if (StringUtils.isBlank(str)) return !mandatory;
        str = str.trim();
        if (str.length() > maxLength) return false;
        return ptn.matcher(str)
                .matches();
    }

    public static InputStream getDataResource(String fileName) throws IOException {
        return getDataResource(fileName, null);
    }

    public static InputStream getDataResource(String fileName, Holder<String> outFilePath) throws IOException {
        // expend system properties varaiables
        fileName = expandEnvVars(fileName);

        // 1. check with data directory home
        if (StringUtils.isNotBlank(GatewayConstant.GATEWAY_HOME)) {
            File f = new File(GatewayConstant.GATEWAY_HOME + File.separatorChar + fileName);
            if (f.exists() && f.isFile()) {
                if (outFilePath != null) {
                    return FileUtils.openInputStream(f);
                }
                return FileUtils.openInputStream(f);
            }
        }
        // 2. check with absolute path
        {
            File f = new File(fileName);
            if (f.exists() && f.isFile()) {
                if (outFilePath != null) {
                    return FileUtils.openInputStream(f);
                }
                return FileUtils.openInputStream(f);
            }
        }
        // 3. return with class path resource
        {
            if (outFilePath != null) {
                return Objects.requireNonNull(CommonUtils.class.getResource(fileName))
                        .openStream();

            }
            return Objects.requireNonNull(CommonUtils.class.getResource(fileName))
                    .openStream();
        }
    }

    public static String expandEnvVars(String text) {
        if (text == null || text.isEmpty()) return text;
        Map<String, String> envMap = System.getenv();
        String pattern = "\\$\\{(.+)\\}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(text);
        while (matcher.find()) {
            String matchGroup = matcher.group(1);
            String envValue = envMap.get(matchGroup);
            if (envValue == null) {
                // try system property
                envValue = System.getProperty(matchGroup);
                if (envValue != null) {
                    envValue = envValue.replace("\\", "\\\\");
                } else envValue = "";
            } else {
                envValue = envValue.replace("\\", "\\\\");
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            text = subexpr.matcher(text)
                    .replaceAll(envValue);
        }
        return text;
    }

    public static String escapeCharactersForLikeExpression(String data) {
        if (data == null) return data;
        return data.replace("!", "\\!")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    public static String getEscapeCharacter() {
        return "\\";
    }

    public static String getISONormalizedDateTime(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(java.time.ZoneId.of("GMT"));
        return instantFormatter(date, format);
    }

    public static String getISONormalizedDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatter(date, format);
    }

    public static String getISODateTimeType1(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getISODateTimeType1(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return dateFormatter(date, format);
    }

    public static String getISODateTimeType2(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getISODateTimeType2(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormatter(date, format);
    }

    public static String getDateTimeType1(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getDateTimeType1(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormatter(date, format);
    }

    public static String getDateTimeType2(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getDateTimeType2(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");// java.util.Date does not support
        // micro-seconds
        return dateFormatter(date, format);
    }

    public static String getDateTimeType3(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getDateTimeType3(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormatter(date, format);
    }

    public static String getDate(Instant date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(java.time.ZoneId.systemDefault());
        return instantFormatter(date, format);
    }

    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter(date, format);
    }

    public static String dateFormatter(Date date, DateFormat format) {
        return format.format(date);
    }

    // Java 8 Time API Support
    public static String instantFormatter(Instant date, DateTimeFormatter format) {
        return format.format(date);
    }


    public static boolean containsHanScript(String s) {
        return s.codePoints()
                .anyMatch(codepoint -> Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
    }

    public static void trimStringsToNull(Object o) {
        for (Field f : o.getClass()
                .getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getType()
                        .equals(String.class)) {
                    String value = (String) f.get(o);
                    f.set(o, StringUtils.trimToNull(value));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public  static synchronized void runAbnormalShell(String errorMessage,String systemDestName) throws Exception {
//        if(!GatewayConstant.isHaveCalled){
//            SystemLogger.info("into runAbnormalShell");
//            if (StringUtils.isBlank(GatewayConstant.SHELL_PATH)) {
//                SystemLogger.error("Abnormal SHELL_PATH is empty,please set path for shell file");
//            }
//            File file = new File(GatewayConstant.SHELL_PATH);
//            if (!file.exists()) {
//                SystemLogger.error("Could not found shell file,please check path is right");
//            }
////            Process ps = Runtime.getRuntime().exec(GatewayConstant.SHELL_PATH);
//
////            Process psNew = Runtime.getRuntime()
////                    .exec("sh" + StringUtils.SPACE + GatewayConstant.SHELL_PATH + StringUtils.SPACE + systemDestName + StringUtils.SPACE + "'" + errorMessage + "'");
////            psNew.waitFor(5, TimeUnit.SECONDS);
////            psNew.destroy();
//        Process ps = Runtime.getRuntime()
//                .exec(new String[]{GatewayConstant.SHELL_PATH, systemDestName, errorMessage});
//        ps.waitFor(5, TimeUnit.SECONDS);
//        ps.destroy();
//            SystemLogger.info("Run Abnormal shell script, file path = {0}",GatewayConstant.SHELL_PATH);
//            GatewayConstant.isHaveCalled = true;
//        }
//
//
//    }

    public static void runNormalShell() throws Exception {
        SystemLogger.info("into runNormalShell");
        if (StringUtils.isBlank(GatewayConstant.NORMAL_SHELL)) {
            SystemLogger.error("NORMAL_SHELL is empty,please set path for shell file");
        }
        File file = new File(GatewayConstant.NORMAL_SHELL);
        if (!file.exists()) {
            SystemLogger.error("Could not found shell file,please check path is right");
        }
//            Process ps = Runtime.getRuntime().exec(GatewayConstant.SHELL_PATH);
        Process ps = Runtime.getRuntime()
                .exec("sh" + StringUtils.SPACE + GatewayConstant.NORMAL_SHELL + StringUtils.SPACE + GatewayConstant.SYSTEM_DEST_NAME);
        ps.waitFor(5, TimeUnit.SECONDS);
        ps.destroy();
        SystemLogger.info("Run normal shell script,shell file path = {0}", GatewayConstant.NORMAL_SHELL);

    }
}
