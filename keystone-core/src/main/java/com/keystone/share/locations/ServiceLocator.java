package com.keystone.share.locations;

import java.util.List;

/**
 * 
 * @author wuqq
 *
 */
public interface ServiceLocator {

	/**
	 * 
	 * @param business
	 * @param service
	 * @return
	 */
	List<ServiceLocation> locate(String business, String service) ;
	
	
	
}
