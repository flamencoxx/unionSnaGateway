package com.iaspec.uniongatewayserver.constant;

import com.iaspec.uniongatewayserver.exception.ServiceException;
import com.iaspec.uniongatewayserver.model.ExceptionEnum;
import com.iaspec.uniongatewayserver.service.PropertyService;
import com.iaspec.uniongatewayserver.util.CommandUtils;
import com.iaspec.uniongatewayserver.util.ExitSystemUtil;
import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Flamenco.xxx
 * @date 2023/4/12  15:12
 */
public class GatewayConstantLoader {

    public static String NUMBER = "^[0-9]*$";

    public static void reloadProperties(PropertyService propertyService) {
        try {
            SystemLogger.infoMethod(GatewayConstantLoader.class, "reloadProperties", true, new String[]{"propertyService"}, propertyService);

            GatewayConstant.TIME_INTERVAL_LONG = propertyService.getLongValue(GatewayConstant.PROP_KEY_TIME_INTERVAL_LONG, 500L);

            GatewayConstant.TIME_INTERVAL_SHORT = propertyService.getLongValue(GatewayConstant.PROP_KEY_TIME_INTERVAL_SHORT, 50L);

            GatewayConstant.ERROR_COUNT_LIMIT = propertyService.getIntegerValue(GatewayConstant.PROP_KEY_ERROR_COUNT_LIMIT, 10);

            GatewayConstant.FATAL_ERROR_STR = propertyService.getStringValue(GatewayConstant.PROP_KEY_FATAL_ERROR, StringUtils.EMPTY);

            GatewayConstant.WHITE_LIST_STR = propertyService.getStringValue(GatewayConstant.PROP_KEY_NORMAL_RETURN_CODE, null);

            GatewayConstant.REQUEST_LEN = propertyService.getIntegerValue(GatewayConstant.PROP_KEY_REQUEST_LEN, 2048);

            GatewayConstant.IS_EBC_OR_ASCII = propertyService.getBooleanValue(GatewayConstant.PROP_KEY_IS_EBC, true);

            GatewayConstant.abnormalShell.set(new File(propertyService.getStringValue(GatewayConstant.PROP_KEY_ABNORMAL_SHELL_PATH, "")));

            GatewayConstant.normalShell.set(new File(propertyService.getStringValue(GatewayConstant.PROP_KEY_NORMAL_SHELL_PATH, "")));

            GatewayConstant.isDuplex.set(propertyService.getBooleanValue(GatewayConstant.PROP_KEY_IS_DUPLEX, false));

            GatewayConstant.MAX_RETIES = propertyService.getIntegerValue(GatewayConstant.PROP_KEY_MAX_RETIES, 5);

            GatewayConstant.RETRY_INTERVAL = 5000L;

            GatewayConstant.CLIENT_CONNECT_TIMEOUT = propertyService.getIntegerValue(GatewayConstant.PROP_KEY_CLIENT_CONNECT_TIMEOUT, 5000);

//            白名单
            if (StringUtils.isNotBlank(GatewayConstant.WHITE_LIST_STR)) {
                String[] whiteListArray = GatewayConstant.WHITE_LIST_STR.split(",");
                GatewayConstant.whiteListCode = Stream.of(whiteListArray)
                        .map(String::trim)
                        .filter(GatewayConstantLoader::isStr2Num)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
                if (GatewayConstant.whiteListCode.isEmpty()){
                    SystemLogger.error("White list config have some problem. Please check again");
                    CommandUtils.runAbnormalShell("White list config have some problem. Please check again");
                    ExitSystemUtil.exitSystem(ExceptionEnum.WHITE_LIST_EMPTY, ExceptionEnum.WHITE_LIST_EMPTY.getMsg());
                }
            }else {
                SystemLogger.error("White list config have some problem. Please check again");
                CommandUtils.runAbnormalShell("White list config have some problem. Please check again");
                ExitSystemUtil.exitSystem(ExceptionEnum.WHITE_LIST_EMPTY, ExceptionEnum.WHITE_LIST_EMPTY.getMsg());
            }

            //        黑名单处理
            if (StringUtils.isNotBlank(GatewayConstant.FATAL_ERROR_STR)) {
                String[] fatalErrorArray = GatewayConstant.FATAL_ERROR_STR.split(",");
                GatewayConstant.fatalErrorSet = Stream.of(fatalErrorArray)
                        .map(String::trim)
                        .filter(GatewayConstantLoader::isStr2Num)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
                if (GatewayConstant.fatalErrorSet.isEmpty()) {
                    SystemLogger.error("Fatal error config have some problem. Please check again");
                }
            } else {
                SystemLogger.error("Fatal error config have some problem. Please check again");
            }

            String systemDestName = System.getProperty(GatewayConstant.JAVA_D_SYSTEM_DEST_NAME, null);
            if (StringUtils.isNotBlank(systemDestName)) {
                GatewayConstant.SYSTEM_DEST_NAME = systemDestName;
            } else {
                GatewayConstant.SYSTEM_DEST_NAME = propertyService.getStringValue(GatewayConstant.PROP_KEY_SYSTEM_DEST_NAME, null);
            }
            if (StringUtils.isBlank(GatewayConstant.SYSTEM_DEST_NAME)) {
                SystemLogger.error("System Dest Name is Empty");
                CommandUtils.runAbnormalShell("System Dest Name is Empty");
                ExitSystemUtil.exitSystem(ExceptionEnum.SYSTEM_NAME_EMPTY, ExceptionEnum.SYSTEM_NAME_EMPTY.getMsg());
            }
            SystemLogger.info("System Dest Name is : {0}", GatewayConstant.SYSTEM_DEST_NAME);

            if (!GatewayConstant.abnormalShell.get()
                    .isFile() || !GatewayConstant.abnormalShell.get()
                    .exists() || !StringUtils.endsWithIgnoreCase(GatewayConstant.abnormalShell.get()
                    .getName(), ".sh")) {
                SystemLogger.error("Abnormal shell config error,please check file is exit and suffix with .sh",new String[]{},new ServiceException("AbnormalShell config error"));
            }

            if (!GatewayConstant.normalShell.get()
                    .isFile() || !GatewayConstant.normalShell.get()
                    .exists() || !StringUtils.endsWithIgnoreCase(GatewayConstant.normalShell.get()
                    .getName(), ".sh")) {
                SystemLogger.error("Normal shell config error,please check file is exit and suffix with .sh",new String[]{},new ServiceException("NormalShell config error"));
            }



        } catch (ServiceException e) {
            SystemLogger.error("Occurs a error when gateway constant load",new String[]{e.getMessage()},e);
        } catch (Throwable e){
           SystemLogger.error("Fail to reloadProperties",new String[] { e.getMessage() }, e);
        }finally {
        }


    }






    public static boolean isStr2Num(String str) {
        Pattern pattern = Pattern.compile(NUMBER);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }




}
