package com.cnautosoft.silver.client;

import java.io.StringReader;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesManager {
	private static final Logger logger = LogManager.getLogger(PropertiesManager.class);
	
	private static PropertiesManager instance;
	private Properties properties = null;
	private PropertiesManager() {}
	private boolean inited = false;

	public static PropertiesManager getInstance(){
		if(instance==null) {
			synchronized(PropertiesManager.class){
				if(instance==null) {
					instance = new PropertiesManager();
				}
			}
		}
		return instance;
	}
	
	public String getProperty(String key) {
		if(properties==null)return null;
		String val = properties.getProperty(key);
		if(val!=null)return val.trim();
		else return null;
	}

	public synchronized void init(String host, int port, String groupId, String dataId) {
		if(inited)return;
		SilverClient sc = null;;
		try {
			sc = new SilverClient(host, port, groupId, dataId);
			sc.addPropertyChangedListener(new PropertyChangedListener() {
				@Override
				public void onChanged(String propertiesStr) {
					try {
						Properties properties = new Properties();
						System.out.println(propertiesStr);
						properties.load(new StringReader(propertiesStr));
						PropertiesManager.this.properties = properties ;
					}
					catch(Exception e) {
						logger.error(e);
					}
				}
			});
			inited = true;
		} catch (Exception e) {
			logger.error(e);
		}
		
		
	}
	
	
}
