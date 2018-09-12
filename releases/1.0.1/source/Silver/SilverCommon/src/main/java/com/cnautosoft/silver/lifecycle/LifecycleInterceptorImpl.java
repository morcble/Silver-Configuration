package com.cnautosoft.silver.lifecycle;

import com.cnautosoft.h2o.core.aop.LifecycleInterceptor;
import com.cnautosoft.h2o.properties.ConfigUtil;
import com.cnautosoft.server.SimpleHtmlServer;
import com.cnautosoft.silver.core.ConfigManager;

public class LifecycleInterceptorImpl implements LifecycleInterceptor{

	@Override
	public void beforeInitContext() {
		
	}

	@Override
	public void afterInitContext() {
		ConfigManager.getInstance();
        try {
			new SimpleHtmlServer().run(
					Integer.parseInt(ConfigUtil.getProperty("web.frontend.port"))
					, ConfigUtil.getProperty("web.frontend.context")
					,ConfigUtil.getProperty("web.frontend.html")
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
