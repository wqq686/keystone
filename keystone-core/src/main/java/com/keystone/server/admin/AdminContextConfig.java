package com.keystone.server.admin;

import com.keystone.server.config.AppContextConfig;

public class AdminContextConfig extends AppContextConfig {

	
	public AdminContextConfig(String contextName, String contextRoot, int minThreadPoolSize, int maxThreadPoolSize, int threadPoolQueueSize, long waitInQueue, int logRate, int flow) {
		super(contextName, contextRoot, minThreadPoolSize, maxThreadPoolSize, threadPoolQueueSize, waitInQueue, logRate, flow);
	}
	
	
	
	
}
