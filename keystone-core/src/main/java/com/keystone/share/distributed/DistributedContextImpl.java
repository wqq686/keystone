package com.keystone.share.distributed;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

class DistributedContextImpl implements DistributedContext {
	
	private Map<String, String> container = new HashMap<>() ;
	
	@Override
	public void clear() {
		container.clear(); 
	}

	@Override
	public String get(String name) {
		return container.get(name) ;
	}

	@Override
	public Set<String> keySet() {
		return container.keySet() ;
	}

	@Override
	public void put(String name, String value) {
		container.put(name, value) ;
	}

	@Override
	public int size() {
		return container.size() ;
	}

	
	@Override
    public String toString() {
        Iterator<Entry<String,String>> i = container.entrySet().iterator();
        if (! i.hasNext()) return "{}";

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (;;) {
        	Entry<String,String> e = i.next();
            String key = e.getKey(); String value = e.getValue();
            builder.append(key).append('=').append(value);
            if (! i.hasNext())
                return builder.append('}').toString();
            builder.append(',').append(' ');
        }
    }
}
