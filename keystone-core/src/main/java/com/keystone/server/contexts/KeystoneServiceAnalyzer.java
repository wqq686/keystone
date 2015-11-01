package com.keystone.server.contexts;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.support.assembly.ClassFile;
import com.keystone.support.assembly.LocalVariableAttribute;
import com.keystone.support.common.TypesUtils;

public class KeystoneServiceAnalyzer {

	
	
	/**
	 * 
	 * @param api
	 * @param service
	 * @return
	 */
	public static ServiceSkeleton analyzeService(String name, Class<?> apiClass, Object service) {
		Map<String, MethodSkeleton> methodContainer = analyzeServiceMethods(apiClass, service) ;
		ServiceSkeleton skeleton = new ServiceSkeleton(name, apiClass, service, methodContainer) ;
		return skeleton ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param apiClass
	 * @param service
	 * @return
	 */
	public static Map<String, MethodSkeleton> analyzeServiceMethods(Class<?> apiClass, Object service) {
		Map<String, MethodSkeleton> methodContainer = new HashMap<String, MethodSkeleton>() ;
		
		Map<String, Method> apiMethods = declaredMethods(apiClass), implMethods = declaredMethods(service.getClass()) ;
		
		ClassFile classFile = ClassFile.createClassFile(service.getClass().getName());
		for(String name : apiMethods.keySet())
		{
			Method apiMethod = apiMethods.get(name), implMethod = implMethods.get(name) ;
			if(implMethod==null)
			{
				throw new IllegalStateException("ImplMethodNotFound(methodName="+apiMethod.getName()+") @ "+service.getClass().getName()) ;
			}
			
			Type[] parameterTypes = apiMethod.getGenericParameterTypes() ;
			int len = parameterTypes==null ? 0 : parameterTypes.length ;
			String[] parameterNames = new String[len] ; Object[] parameterMarkers = new Object[len] ;
			
			LocalVariableAttribute argsAttrs = classFile==null ? null : classFile.getMethodLocalVariableAttribute(implMethod.getName());
			
			for(int index=0, order=1; index<len; index++, order++) {
				Type type = parameterTypes[index] ;
				parameterNames[index] = argsAttrs!=null ? argsAttrs.methodVariableName(order) : "args" + order;
				parameterMarkers[index] = TypesUtils.foundDefualtValue(type) ;
			}
			
			Type returnType = implMethod.getGenericReturnType();
			MethodSkeleton skeleton = new MethodSkeleton(implMethod, parameterTypes, parameterNames, parameterMarkers, returnType) ;
			methodContainer.put(apiMethod.getName(), skeleton) ;
		}		
		return methodContainer ;
	}
	
	
	
	
	/**
	 * 这个方法之所以只放在这里而不放在工具类中...是因为它只适用于目前不支持方法重载的情况.
	 * 
	 * @param klass
	 * @return
	 */
	private static Map<String, Method> declaredMethods(Class<?> klass) {
		Map<String, Method> methods = new LinkedHashMap<>() ;
		for(; klass != null && klass!=Object.class ; klass = klass.getSuperclass()) {
			Method[] array = klass.getDeclaredMethods() ;
			for(Method m : array)
			{
				if(!methods.containsKey(m.getName())) methods.put(m.getName(), m) ;
			}
		}
		return methods ;
	}
	
	
	public static Type[] getMethodParameterTypes(String contextName, String serviceName, String methodName) {
		MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(contextName, serviceName, methodName, true) ;
		return skeleton.getArgsTypes() ;
	}
}
