package com.iaspec.uniongatewayserver.util;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Flamenco.xxx
 * @date 2023/4/27  11:29
 */
public class CommandUtils {

    public static void runAbnormalShellWithFunc(String errorMessage , Function<String,Object> func){
        try {
            runAbnormalShell(errorMessage);
        } catch (Exception e) {
            func.apply(e.getMessage());
        }
    }


    public static synchronized void runAbnormalShell(String errorMessage) throws Exception {
        if(!GatewayConstant.isHaveCall.get()){
            SystemLogger.info("into runAbnormalShell");
//            Process ps = Runtime.getRuntime().exec(GatewayConstant.SHELL_PATH);

//            Process psNew = Runtime.getRuntime()
//                    .exec("sh" + StringUtils.SPACE + GatewayConstant.SHELL_PATH + StringUtils.SPACE + systemDestName + StringUtils.SPACE + "'" + errorMessage + "'");
//            psNew.waitFor(5, TimeUnit.SECONDS);
//            psNew.destroy();
            GatewayConstant.isAbnormalShutdown.set(true);
            Process ps = Runtime.getRuntime()
                    .exec(new String[]{GatewayConstant.abnormalShell.get().getAbsolutePath(), GatewayConstant.SYSTEM_DEST_NAME, errorMessage});
            ps.waitFor(5, TimeUnit.SECONDS);
            ps.destroy();
            SystemLogger.info("Run Abnormal shell script, file path = {0}",GatewayConstant.abnormalShell.get().getAbsolutePath());
            GatewayConstant.isHaveCall.set(true);
        }


    }

    public static void runNormalShell() throws Exception {
        SystemLogger.info("into runNormalShell");
//            Process ps = Runtime.getRuntime().exec(GatewayConstant.SHELL_PATH);
        Process ps = Runtime.getRuntime()
                .exec("sh" + StringUtils.SPACE + GatewayConstant.normalShell.get().getAbsolutePath() + StringUtils.SPACE + GatewayConstant.SYSTEM_DEST_NAME);
        ps.waitFor(5, TimeUnit.SECONDS);
        ps.destroy();
        SystemLogger.info("Run normal shell script,shell file path = {0}", GatewayConstant.normalShell.get().getAbsolutePath());

    }

}
