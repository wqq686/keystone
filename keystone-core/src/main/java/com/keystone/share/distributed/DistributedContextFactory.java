package com.keystone.share.distributed;

import java.util.HashMap;
import java.util.Map;

/**
 * 有改进空间
 * @author wuqq
 *
 */
public class DistributedContextFactory {

	private static final ThreadLocal<DistributedContext> contextHloder = new ThreadLocal<DistributedContext>() {
        protected DistributedContext initialValue() {
            return new DistributedContextImpl();
        }
    };
    
    
    
    /**
     * 
     * @return
     */
    public static DistributedContext getDistributedContext() {
    	return contextHloder.get() ;
    }
    
    

    /**
     * 从Map创建一个合适的线程上线文.
     * 
     * @param map
     */
    public static DistributedContext buildDistributedContext(Map<String, String> map) {
    	DistributedContext dc = getDistributedContext();
    	if(map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                dc.put(entry.getKey(), entry.getValue());
            }
    	}
        return dc ;
    }

    
    
    
    /**
     * 导出上线程下文数据到一个map
     * 
     * @return
     */
    public static Map<String, String> exportDistributedContext() {
        DistributedContext dc = getDistributedContext();
        HashMap<String, String> map = new HashMap<String, String>(dc.size());
        exportDistributedContext(map);
        return map;
    }

    
    
    
    public static void exportDistributedContext(Map<String, String> map) {
        DistributedContext dc = getDistributedContext();
        for (String key : dc.keySet()) {
            map.put(key, dc.get(key));
        }
    }

    
    
    
    /**
     * 清除线程上下文相关变量
     */
    public static void clearDistributedContext() {
        DistributedContext dc = getDistributedContext();
        if(dc!=null) {
        	dc.clear();
        }
    }
}
