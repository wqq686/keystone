package com.keystone.remoting.locator;

/**
 * 貌似没有存在的必要...
 * 
 * @author wuqq
 *
 */
@Deprecated
public interface ServiceLocationChangedListener {

	/**
	 * 
	 * @param bussiness
	 * @param service
	 */
	void onLocationChanged(String bussiness, String service) ;
	
}
