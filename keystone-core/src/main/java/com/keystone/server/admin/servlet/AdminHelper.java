package com.keystone.server.admin.servlet;

import java.util.ArrayList;
import java.util.List;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.services.MethodSkeleton;

public class AdminHelper {

	
	public static List<String> getMethodArgsName(String contextName, String serviceName, String methodName) {
		MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(contextName, serviceName, methodName, true) ;
		List<String> nameList = new ArrayList<>();
		String[] array = skeleton.getArgsNames() ;
		for(String a : array) {
			nameList.add(a) ;
		}
		return nameList ;
	}
	
	
	
}
