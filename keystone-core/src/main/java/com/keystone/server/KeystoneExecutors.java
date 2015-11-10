package com.keystone.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.keystone.server.config.AppContextConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.support.threadpool.TaskQueue;
import com.keystone.support.threadpool.TaskThreadFactory;
import com.keystone.support.threadpool.TaskThreadPoolExecutor;



/**
 * 抽取自tomcat, fix掉JDK线程池坑爹的延时放大问题
 * 
 * @author wuqq
 *
 */
public class KeystoneExecutors {

	/** */
	private final static ConcurrentHashMap<String, FutureTask<Executor>> pools = new ConcurrentHashMap<String, FutureTask<Executor>>();
	
	private static String NULL_APPCONTEXT = "admin" ;
	
	
	/**
	 * 
	 * @param appContext
	 * @param command
	 */
	public static void execute(KeystoneAppContext appContext, Runnable command) {
		if(appContext==null) {
			appContext = KeystoneResourcesManager.getAppContext(NULL_APPCONTEXT, true) ;
		}
		Executor executor = getExecutor(appContext.getContextConfig()) ;
		executor.execute(command);
//		if(appContext!=null) {
//			Executor executor = getExecutor(appContext.getContextConfig()) ;
//			executor.execute(command);
//		} 
	}

	
	
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	public static Executor getExecutor(final AppContextConfig config) {
		try
		{
			//TODO: use key or use name ?
			FutureTask<Executor> executorTask = pools.get(config.getContextName());
			if (null != executorTask) 
			{
				return executorTask.get();
			}
			else
			{
				Callable<Executor> creator = new Callable<Executor>() {
					@Override
					public Executor call() throws Exception {
						TaskQueue taskQueue = new TaskQueue(config.getThreadPoolQueueSize());
						TaskThreadPoolExecutor pool = new TaskThreadPoolExecutor(config.getMinThreadPoolSize(), 
								config.getMaxThreadPoolSize(), 120, TimeUnit.SECONDS, taskQueue, 
								new TaskThreadFactory(config.getContextName()+"-threadpool-exec-"));
						taskQueue.setParent(pool);
						return pool;
					}
				};
				
				FutureTask<Executor> newTask = new FutureTask<Executor>(creator);
				executorTask = pools.putIfAbsent(config.getContextName(), newTask);
				if (null == executorTask) {
					executorTask = newTask;
					executorTask.run();
				}
				return executorTask.get();
			}
		} catch(Exception e){throw new IllegalStateException(e);}
	}
	

	/**
	 * 
	 */
	public static synchronized void shutdown() {
		for(FutureTask<Executor> task : pools.values()) 
		{
			try
			{
				Executor e = task.get() ;
				if(e instanceof java.util.concurrent.ThreadPoolExecutor) 
				{
					((java.util.concurrent.ThreadPoolExecutor)e).shutdown() ;
				}
			}catch(Exception ignore){}
		}
	}
}
