package com.keystone.share.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author wuqq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Router 
{
	/**
	 * 
	 * @return
	 */
	String business() ;
	
	/**
	 * 
	 * @return
	 */
	String service();
	
}
