package com.keystone.server.protocol;


/**
 * 
 * @author wuqq
 *
 */
public class ProtocolHelper {

	
	/**
	 * 
	 * @param magic
	 * @return
	 */
	public static boolean isKeystone(byte magic) {
		return magic == 'K' ;
	}
	
	
	
	/**
	 * 
	 * @param magic
	 * @return
	 */
	public static boolean isHTTP(byte magic) {
		return magic =='P' || magic == 'G' ;
	}
	
	
}
