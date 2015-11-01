package com.keystone.share.locations;

import java.util.Arrays;

import com.keystone.share.services.Router;

/**
 * 
 * @author wuqq
 *
 */
public class Locators {
	
	/** */
	public static int DEFAULT_SERVICE_CONNECT_TIMEOUT = 5000 ;
	
	/** */
	public static int DEFAULT_SERVICE_READ_TIMEOUT = 10000 ;

	/** */
	public static String KEYSTONE_RPC_ZOOKEEPER_ROOT = "/keystone/rpc" ;
	
	
	/**
	 * 
	 * @param business
	 * @return
	 */
	public static String getBusinessPath(String business) {
		return KEYSTONE_RPC_ZOOKEEPER_ROOT + "/" + business ;
	}
	
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @return
	 */
	public static String getServicePath(String business, String service) {
		return getBusinessPath(business) +"/" + service ;
	}
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param host
	 * @param port
	 * @param context
	 * @return
	 */
	public static String getLocationPath(String business, String service, String host, int port, String context) {
		String path = getServicePath(business, service) ;
		host = host.replaceAll("\\.", "") ;
		String name = host + "_" + port + "_" +context ;
		return builderPath(path, name) ;
	}
	
	/**
	 * 
	 * @param paths
	 * @return
	 */
	public static String builderPath(Object...paths) {
		String first = paths[0].toString() ;
		if(!first.startsWith("/")) {
			first = "/" + first ;
		}
		StringBuilder builder = new StringBuilder(first) ;
		for(int index=1; index<paths.length; index++) {
			builder.append("/").append(paths[index]) ;
		}
		return builder.toString() ;
	}
	
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String[] spiltPath(String path) {
		path = path.replaceFirst(KEYSTONE_RPC_ZOOKEEPER_ROOT, "") ;
		String[] array = path.split("/") ;
		return new String[]{array[1], array[2]} ;
	}
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @return
	 */
	public static String getServiceLocationKey(String business, String service) {
		String key = (business + "." + service).intern() ;
		return key ;
	}
	
	
	/**
	 * 
	 * @param api
	 * @return
	 */
	
	public static Router extractRouter(Class<?> api) {
		Router router = api.getAnnotation(Router.class) ;
		if(router==null) throw new IllegalStateException("Annotation[Rounter]NotFound[api=" +api.getName()+"]") ;
		return router ;
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String path = getServicePath("a", "b") ;
		System.out.println(Arrays.toString(spiltPath(path)));
	}
}
