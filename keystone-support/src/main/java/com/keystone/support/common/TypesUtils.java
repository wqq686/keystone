package com.keystone.support.common;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author wuqq
 *
 */
public class TypesUtils {
	
	/**
	 * 
	 */
	private static Map<Class<?>, Object> defaultValues = new ConcurrentHashMap<Class<?>, Object>() ;
	
	
	public static final Boolean STAMP_BOOLEAN = Boolean.TRUE;
	public static final Byte STAMP_BYTE = Byte.valueOf((byte) 0);
	public static final Short STAMP_SHORT = Short.valueOf((short) 0);
	public static final Float STAMP_FLOAT = Float.valueOf(0);
	public static final Integer STAMP_INT = Integer.valueOf(0);
	public static final Long STAMP_LONG = Long.valueOf(0);
	public static final Double STAMP_DOUBLE = Double.valueOf(0);
	public static final String STAMP_STRING = "";
	public static final Object STAMP_OBJECT = new Object();
	
	
	public static final boolean[] STAMP_BOOLEAN_ARRAY = new boolean[] { true };
	public static final byte[] STAMP_BYTE_ARRAY = new byte[] { 0 };
	public static final short[] STAMP_SHORT_ARRAY = new short[] { 0 };
	public static final int[] STAMP_INT_ARRAY = new int[] { 0 };
	public static final long[] STAMP_LONG_ARRAY = new long[] { 0 };
	public static final float[] STAMP_FLOAT_ARRAY = new float[] { 0 };
	public static final double[] STAMP_DOUBLE_ARRAY = new double[] { 0 };
	
	
//	public static final Map<String, String> STAMP_MAP = new HashMap<String, String>();
//	static { STAMP_MAP.put("", ""); }
	
	/**
	 * 判断Class是否是boolean(Boolean)、byte.class(Byte)、short(Short)、int(Integer)、
	 * long(Long)、float(Float)、double(Double)、String等基本数据类型
	 * 
	 * @param klass
	 * @return
	 */
	public static boolean isPrimitive(Class<?> klass) {
		return     klass == boolean.class || klass == Boolean.class
				|| klass == byte.class    || klass == Byte.class
				|| klass == short.class   || klass == Short.class
				|| klass == int.class     || klass == Integer.class
				|| klass == long.class    || klass == Long.class
				|| klass == float.class   || klass == Float.class
				|| klass == double.class  || klass == Double.class
				|| klass == String.class;
	}

	
	
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	public static boolean isMap(Class<?> klass) {
		return Map.class.isAssignableFrom(klass);
	}

	
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	public static boolean isCollection(Class<?> klass) {
		return Collection.class.isAssignableFrom(klass);
	}

	
	
	/**
	 * 取得java基本数据类型或者数组类型或者JavaBean的实例
	 * 
	 * @param klass
	 * @return
	 */
	public static Object getDefaultValue(Class<?> klass) {
		if (klass == boolean.class || klass == Boolean.class) {
			return STAMP_BOOLEAN;
		} else if (klass == byte.class || klass == Byte.class) {
			return STAMP_BYTE;
		} else if (klass == short.class || klass == Short.class) {
			return STAMP_SHORT;
		} else if (klass == int.class || klass == Integer.class) {
			return STAMP_INT;
		} else if (klass == long.class || klass == Long.class) {
			return STAMP_LONG;
		} else if (klass == float.class || klass == Float.class) {
			return STAMP_FLOAT;
		} else if (klass == double.class || klass == Double.class) {
			return STAMP_DOUBLE;
		} else if (klass == String.class) {
			return STAMP_STRING;
		} else if (klass == boolean[].class) {
			return STAMP_BOOLEAN_ARRAY;
		} else if (klass == byte[].class) {
			return STAMP_BYTE_ARRAY;
		} else if (klass == short[].class) {
			return STAMP_SHORT_ARRAY;
		} else if (klass == int[].class) {
			return STAMP_INT_ARRAY;
		} else if (klass == long[].class) {
			return STAMP_LONG_ARRAY;
		} else if (klass == float[].class) {
			return STAMP_FLOAT_ARRAY;
		} else if (klass == double[].class) {
			return STAMP_DOUBLE_ARRAY;
		}
		//JUST FOR TRY...
		if(klass==void.class || klass==Void.class)
		{
			return null ;
		}
		else if(klass==Object.class)
		{
			return STAMP_OBJECT ;
		}
		
		Object stamp = defaultValues.get(klass);
		
		if (stamp == null) 
		{
			if (klass.isArray()) {
				Class<?> componentType = klass.getComponentType();
				Object[] array = (Object[]) Array.newInstance(componentType, 1);
				Object e = getDefaultValue(componentType);
				array[0] = e;
				stamp = array;
			} else {
				stamp = CommonUtils.newInstance(klass);
			}
			defaultValues.put(klass, stamp);
		}
		return stamp;
	}
	
	
	
	
	/**
	 * 根据类型Type得到其实例
	 * 
	 * @param type
	 *            Type
	 * @return
	 */
	public static Object foundDefualtValue(Type type) {
		if (type instanceof Class<?>) 
		{
			Class<?> klass = (Class<?>) type;
			return getDefaultValue(klass) ;
		}
		else if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> klass = (Class<?>) parameterizedType.getRawType();
			
			if(isMap(klass))
			{
				Type[] types = parameterizedType.getActualTypeArguments();
				Type keyType = types[0];
				Type valueType = types[1];

				Object key = foundDefualtValue(keyType);
				Object value = foundDefualtValue(valueType);
				Map<Object, Object> map = new HashMap<Object, Object>(1);
				map.put(key, value);
				return map;				
			}
			else if (isCollection(klass)) 
			{//数组不会到这里,到这里的只会是collection
				Type[] types = parameterizedType.getActualTypeArguments();
				Type valueType = types[0];
				Object e = foundDefualtValue(valueType);
				List<Object> list = new ArrayList<Object>(1);
				list.add(e);
				return list;
			}
			else
			{
				return getDefaultValue(klass) ;
			}
		}
		else if (type instanceof GenericArrayType) 
		{// 泛型数组String[]、int[]、Map[]...etc
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			Object component = foundDefualtValue(componentType);
			Object[] array = (Object[]) Array.newInstance(component.getClass(), 1);
			array[0] = component;
			return array;
		}
		throw new IllegalStateException("Type=[" + type + "] illegal.");
	}

	
	
	
	/**
	 * 将data转换为Class类型的值
	 * @param data
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cashFor(Object data, Class<?> klass) {
		Object to = foundDefualtValue(klass) ;
		return (T)cashTo(data, to) ;
	}
	
	
	
	/**
	 * 将data转换为to类型的值
	 * @param data
	 * @param to
	 * @return
	 */
	public static Object cashTo(Object data, Object to) {
		if(data==null || to==null || to == STAMP_OBJECT) {
			return data ;
		}
		
		Object realValue = null ;
		if(to.getClass()==data.getClass()) {
			realValue = data ;
		} else {
			String stringOfValue = StringUtils.toString(data, "") ;
			if(stringOfValue.isEmpty()) return to ;
			
			if(to==STAMP_DOUBLE)
			{
				realValue = Double.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_LONG)
			{
				realValue = Long.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_INT)
			{
				realValue = Integer.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_BOOLEAN)
			{
				realValue = Boolean.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_FLOAT)
			{
				realValue = Float.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_SHORT)
			{
				realValue = Short.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_BYTE)
			{
				realValue = Byte.valueOf(stringOfValue) ;
			}
			else if(to==STAMP_STRING)
			{
				return stringOfValue;
			}
			else
			{
				throw new IllegalArgumentException("data="+data+", class=" + data.getClass().getName()+", toClass=" + to.getClass().getName()) ;
			}
		}
		return realValue ;
	}
	
	
	
}
