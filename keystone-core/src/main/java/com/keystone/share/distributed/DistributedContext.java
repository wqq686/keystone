package com.keystone.share.distributed;

import java.util.Set;

public interface DistributedContext {
	
    /**
     * clear all context attributes.
     */
    public void clear();

    /**
     * get attribute by name.
     * 
     * @param name
     * @return
     */
    public String get(String name);

    /**
     * get attribute names
     * 
     * @return
     */
    public Set<String> keySet();

    /**
     * put attribute
     * 
     * @param name
     * @param value
     */
    public void put(String name, String value);

    /**
     * get attribute count in context.
     * 
     * @return
     */
    public int size();
}
