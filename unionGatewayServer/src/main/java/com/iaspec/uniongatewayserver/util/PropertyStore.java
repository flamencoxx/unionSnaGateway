package com.iaspec.uniongatewayserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Flamenco.xxx
 * @date 2022/8/16  15:39
 */
public class PropertyStore {


    protected Properties prop;

    private String fileName;

    public void init() {
        reloadConfig();
    }


    public void destroy() {
        prop = null;
    }

    public void reloadConfig() {
        synchronized (this) {
            InputStream is = null;
            try {
                is = CommonUtils.getDataResource(fileName);
            } catch (IOException ignored) {
            }
            if (prop == null) {
                prop = new Properties();
                if (is == null) {
                    return;
                }
            }
            Properties tmp = (Properties) prop.clone();
            prop.clear();
            try {
                prop.load(is);
//                test
//                prop.keySet().forEach(k -> Console.log("key: {}, value: {}",k,prop.get(k)));
            } catch (IOException ioe) {
                prop = tmp;
            } finally {
                try {
                    is.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Properties getProp() {
        return prop;
    }

}
