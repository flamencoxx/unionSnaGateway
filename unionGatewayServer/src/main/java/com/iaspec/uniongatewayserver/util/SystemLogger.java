package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.exception.ServiceException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author Flamenco.xxx
 * @date 2022/8/12  17:36
 */



public class SystemLogger {

    public static final Logger log = LogManager.getLogger(SystemLogger.class);

    public static final ConcurrentMap<Long, String> threadKeyTable = new ConcurrentHashMap<>();

    /**
     * System Logger info
     *
     * @param message
     * @param params
     */
    public static void info(String message, Object... params) {
        if (!log.isInfoEnabled())
            return;
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.info(message);
    }

    /**
     * System Logger info
     *
     * @param message
     */
    public static void info(String message) {
        info(message, (Object[]) null);
    }

    public static void info(String message, Object[] params, Throwable t) {
        if (!log.isInfoEnabled())
            return;
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.info(message, t);
    }

    /**
     * System Logger warning
     *
     * @param message
     * @param params
     * @param t
     */
    public static void warning(String message, Object[] params, Throwable t) {
        if (!log.isWarnEnabled())
            return;
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.warn(message, t);
    }

    /**
     * System Logger warning
     *
     * @param message
     * @param t
     */
    public static void warning(String message, Throwable t) {
        if (!log.isWarnEnabled())
            return;
        warning(message, null, t);
    }

    /**
     * System Logger warning
     *
     * @param message
     */
    public static void warning(String message) {
        if (!log.isWarnEnabled())
            return;
        warning(message, null, null);
    }

    /**
     * System Logger debug
     *
     * @param message
     * @param params
     * @param t
     */
    public static void debug(String message, Object[] params, Throwable t) {
        if (!log.isDebugEnabled())
            return;
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.debug(message, t);
    }

    /**
     * System Logger debug
     *
     * @param message
     * @param params
     */
    public static void debug(String message, Object... params) {
        if (!log.isDebugEnabled())
            return;
        debug(message, params, null);
    }

    /**
     * System Logger debug
     *
     * @param message
     */
    public static void debug(String message) {
        if (!log.isDebugEnabled())
            return;
        debug(message, null, null);
    }

    /**
     * System Logger trace
     *
     * @param message
     * @param params
     * @param t
     */
    public static void trace(String message, Object[] params, Throwable t) {
        if (!log.isTraceEnabled()) return;
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.trace(message, t);
    }

    /**
     * System Logger trace
     *
     * @param message
     * @param params
     */
    public static void trace(String message, Object... params) {
        if (!log.isTraceEnabled()) return;
        trace(message, params, null);
    }

    /**
     * System Logger trace
     *
     * @param message
     */
    public static void trace(String message) {
        if (!log.isTraceEnabled()) return;
        trace(message, null, null);
    }

    public static void infoMethod(Class<?> cls, String methodName, boolean start, String[] argNames, Object... args) {
        if (!isInfoEnabled())
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getSimpleName()).append(".").append(methodName).append("#").append(start ? "start" : "end");
        if (ArrayUtils.isNotEmpty(argNames)) {
            sb.append(" - ");
            for (int i = 0; i < argNames.length; i++)
                sb.append(argNames[i]).append("=[{").append(i).append("}], ");
            sb.delete(sb.length() - 2, sb.length()); // remove the last ', '
        }
        SystemLogger.info(sb.toString(), args);
    }

    public static String getThreadKey() {
        if (threadKeyTable.containsKey(Thread.currentThread().getId()))
            return threadKeyTable.get(Thread.currentThread().getId());
        else
            return " () ";
    }

    public static void infoMethod(Class<?> cls, String methodName, String objectKey, boolean start, String[] argNames,
                                  Object... args) {
        if (!isInfoEnabled())
            return;
        if (!StringUtils.isEmpty(objectKey)) {
            if (start) {
                StringBuffer buf = new StringBuffer();
                buf.append(" (").append(objectKey).append(") ");
                threadKeyTable.put(Thread.currentThread().getId(), buf.toString());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getSimpleName()).append(".").append(methodName).append("#").append(start ? "start" : "end");
        if (ArrayUtils.isNotEmpty(argNames)) {
            sb.append(" - ");
            for (int i = 0; i < argNames.length; i++)
                sb.append(argNames[i]).append("=[{").append(i).append("}], ");
            sb.delete(sb.length() - 2, sb.length()); // remove the last ', '
        }
        SystemLogger.info(sb.toString(), args);
    }

    public static void debugMethod(Class<?> cls, String methodName, boolean start, String[] argNames, Object... args) {
        if (!isDebugEnabled())
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getSimpleName()).append(".").append(methodName).append("#").append(start ? "start" : "end");
        if (ArrayUtils.isNotEmpty(argNames)) {
            sb.append(" - ");
            for (int i = 0; i < argNames.length; i++)
                sb.append(argNames[i]).append("=[{").append(i).append("}], ");
            sb.delete(sb.length() - 2, sb.length()); // remove the last ', '
        }
        SystemLogger.debug(sb.toString(), args);
    }

    public static void debugMethod(Class<?> cls, String methodName, String methodHints, String[] argNames,
                                   Object... args) {
        if (!isDebugEnabled())
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getSimpleName()).append(".").append(methodName);
        if (StringUtils.isNoneBlank(methodHints))
            sb.append("#").append(methodHints);
        if (ArrayUtils.isNotEmpty(argNames)) {
            sb.append(" -> ");
            for (int i = 0; i < argNames.length; i++)
                sb.append(argNames[i]).append("=[{").append(i).append("}], ");
            sb.deleteCharAt(sb.length() - 2); // remove the last ', '
        }
        SystemLogger.debug(sb.toString(), args);
    }

    /**
     * System Logger error
     *
     * @param message
     * @param params
     * @param t
     */
    public static void error(String message, Object[] params, Throwable t) {
        if (params != null && params.length > 0)
            try {
                message = MessageFormat.format(getThreadKey() + message, params);
            } catch (Exception e) {
            }
        log.error(message, t);
    }

    /**
     * System Logger debug
     *
     * @param message
     * @param params
     */
    public static void error(String message, Object... params) {
        error(message, params, null);
    }

    /**
     * System Logger debug
     *
     * @param message
     */
    public static void error(String message) {
        error(message, null, null);
    }

    public static boolean isDebugEnabled() {
        return log.isDebugEnabled();
        // return true;
    }

    public static boolean isInfoEnabled() {
        return log.isInfoEnabled();
        // return true;
    }

    public static void defaultServiceExceptionLog(ServiceException se) {
        switch (se.getLogLevel()) {
            case DEBUG:
                debug(se.getMessage(), null, se);
                break;
            case INFO:
                info(se.getMessage());
                break;
            case WARNING:
                warning(se.getMessage(), se);
                break;
            case ERROR:
                error(se.getMessage(), null, se);
                break;
        }
    }

    public static void defaultThrowableLog(Throwable t) {
        Throwable cause = t.getCause();
        while (cause != null) {
            if (cause instanceof ServiceException) {
                defaultServiceExceptionLog((ServiceException) cause);
                return;
            }
            cause = cause.getCause();
        }
        error(t.getMessage(), null, t);
    }
}

