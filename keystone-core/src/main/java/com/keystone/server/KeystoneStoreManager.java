package com.keystone.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.keystone.server.admin.KeystoneAdminContext;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.IOUtils;

/**
 * 对于access.cache.txt,首先会写入access2.cache.txt中, 然后再重命名为access.cache.txt
 * 
 * @author wuqq
 *
 */
public class KeystoneStoreManager {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator"); 
	
	/** */
	private static String accessFilePath ;
	
	/** */
	private static String accessFilePath2 ;
	
	/** */
	private static Thread worker = null ;
	
	
	
	/**
	 * 
	 */
	public static synchronized void start() {
		initPath();
		
		recover();
		
		startWorker();
		
		System.out.println("[KEYSTONE] The store manager started...");
	}
	
	
	
	
	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static synchronized void stop() {
		if(worker!=null) {
			worker.stop();
		}
	}
	
	
	
	
	
	
	
	/**
	 * 
	 */
	static synchronized void initPath() {
		String root = KeystoneResourcesManager.getServerConfig().getServerRoot() ;
		accessFilePath = IOUtils.mergePaths(root, "store", "access.cache.txt") ;
		accessFilePath2 = IOUtils.mergePaths(root, "store", "access.cache2.txt") ;
	}
	
	
	
	
	
	
	/**
	 * 
	 */
	static synchronized void recover() {
		String root = KeystoneResourcesManager.getServerConfig().getServerRoot() ;
		accessFilePath = IOUtils.mergePaths(root, "store", "access.cache.txt") ;
		File accessFile = IOUtils.getFile(accessFilePath) ;
		if(accessFile!=null) 
		{
			BufferedReader reader = null ;
			try
			{
				reader = new BufferedReader(new FileReader(accessFile)) ;
				String line = null ;
				while((line=reader.readLine())!=null) {
					Map<String, Map<String, Long>> one = JSON.parseObject(line, new TypeReference<Map<String, Map<String, Long>>>(){}) ;
					for(String key : one.keySet()) {
						Map<String, Long> stat = one.get(key) ;
						String[] array = key.split("\\.") ;
						String contextName = array[0], serviceName = array[1], methodName = array[2] ;
						MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(contextName, serviceName, methodName, false) ;
						if(skeleton!=null)
						{
							long accessCount = CommonUtils.get(stat, "accessCount", 0L) ;
							long successCount = CommonUtils.get(stat, "successCount", 0L) ;
							long failedCount = CommonUtils.get(stat, "failedCount", 0L) ;
							long avgCost = CommonUtils.get(stat, "avgCost", 0L) ;
							skeleton.initStatCount(accessCount, successCount, failedCount, avgCost) ;							
						}

					}
				}
			}
			catch(Exception ignore) {} finally { IOUtils.close(reader); }
		}
	}
	
	
	
	
	
	
	/**
	 * 
	 */
	static synchronized void startWorker() {
		worker = new Thread("ksserver-stat-worker") {
			@Override
			public void run() {
				for(;;)
				{
					//1. 
					try{ TimeUnit.SECONDS.sleep(10); }catch(Exception ignore){}
					//2. 
					stat();					
				}
			}
		} ;
		worker.setDaemon(true);
		worker.start();
	}
	
	
	
	/**
	 * 
	 */
	static synchronized void stat() {
		boolean ok = writeAccessFile2() ;
		if(ok)
		{
			File accessFile2 = new File(accessFilePath2), accessFile = new File(accessFilePath) ;
			IOUtils.renameFile(accessFile2, accessFile);
		}
	}
	
	
	
	
	
	
	static boolean writeAccessFile2() {
		BufferedWriter writer = null ; boolean success = false ;
		try
		{
			File accessFile2 = IOUtils.getFile(accessFilePath2) ;
			writer = new BufferedWriter(new FileWriter(accessFile2)) ;
			ConcurrentMap<String, KeystoneAppContext> contexts = KeystoneResourcesManager.getAppcontexts() ;
			for(String name : contexts.keySet())
			{
				KeystoneAppContext kac = contexts.get(name) ;
				if(kac instanceof KeystoneAdminContext) continue ;
				
				String contextName = kac.getContextName() ;
				for( String serviceName : kac.getRemotingContainer().skeletons().keySet() ) {
					ServiceSkeleton skeleton = KeystoneResourcesManager.getServiceSkeleton(contextName, serviceName, false) ;
					if(skeleton==null) continue ;
					
					for(MethodSkeleton mskeleton : skeleton.getMethodContainer().values()) {
						String methodName = mskeleton.getMethod().getName() ;
						String key = contextName + "." + serviceName + "." + methodName ;
						Map<String, Long> stat = new HashMap<>() ;
						stat.put("accessCount", mskeleton.getAccessCount()) ;
						stat.put("successCount", mskeleton.getSuccessCount()) ;
						stat.put("failedCount", mskeleton.getFailedCount()) ;
						stat.put("avgCost", mskeleton.getAvgCost()) ;
						Map<String, Map<String, Long>> stamp = new HashMap<>() ;
						stamp.put(key, stat) ;
						String line = JSON.toJSONString(stamp) ;
						writer.write(line); 
						writer.write(LINE_SEPARATOR);
					}
				}
			}
			success = true ;
			
		} catch(Exception ignore){} finally{ IOUtils.close(writer); }
		
		return success ;
	}
	
}
