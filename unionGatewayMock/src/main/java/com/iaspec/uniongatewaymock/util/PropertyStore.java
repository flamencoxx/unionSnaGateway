package com.iaspec.uniongatewaymock.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyStore {

	protected Properties prop;

	public void init() {
		reloadConfig();
	}

	public void destroy(){
		prop = null;
	}

	/**
	 * Reload Config
	 *
	 */
	public void reloadConfig(){
		synchronized(this){
			InputStream is = null; 
			try {
				is = CommonUtils.getDataResource(fileName);
			} catch (IOException e) {
				SystemLogger.error("Error loading config, e.message={0}",new String[]{e.getMessage()},e);
			}
			if(prop == null) {
				prop = new Properties();
				if (is == null) {
					return;
				}
			}
			Properties tmp = (Properties) prop.clone();
			prop.clear();
			try {
				prop.load(is);
			} catch (IOException ioe) {
				prop = tmp;
			} finally {
				try{
					is.close();
				}catch(Exception e){
					SystemLogger.error("Error closing config",new String[]{e.getMessage()},e);
				}
			}
		}
	}
	/**
	 * @return the prop
	 */
	public Properties getProp() {
		return prop;
	}

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
