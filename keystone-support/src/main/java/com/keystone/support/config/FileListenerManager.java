package com.keystone.support.config;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.keystone.support.utils.Triple;


public class FileListenerManager {

	/**
	 * 
	 */
	private static ConcurrentMap<String, Triple<String, FileEntry, List<FileListener>>> listeners = new ConcurrentHashMap<>() ;
	
	
	static
	{
		for(;!Thread.interrupted();)
		{
			try
			{
				Thread.sleep(1000L);
				for(String pathname : listeners.keySet())
				{
					Triple<String, FileEntry, List<FileListener>> stamp = listeners.get(pathname) ;
					File current = new File(pathname) ;
					if(stamp.getSecond().refresh(current))
					{
						List<FileListener> list = stamp.getThird() ;
						for(FileListener listener : list)
						{
							try{listener.onChanged(current);}catch(Exception e){e.printStackTrace();}
						}
					}
				}
			}catch(Throwable ignore){}
		}
	}
	
	
	
	
	
	/**
	 * 监听某一路径下的文件
	 * 
	 * @param pathname
	 * @param listener
	 */
	public synchronized static void register(String pathname, FileListener listener) {
		register(new File(pathname), listener);
	}
	
	
	
	/**
	 * 监听某一路径下的文件
	 * 
	 * @param watcher
	 */
	public synchronized static void register(File file, FileListener listener) {
		if(file==null || listener==null) throw new IllegalArgumentException("the file=["+file+"] or listener=["+listener+"] is null.") ;
		
		try
		{
			String path = file.getCanonicalPath() ;
			Triple<String, FileEntry, List<FileListener>> stamp = listeners.get(path) ;
			if(stamp==null)
			{
				stamp = new Triple<String, FileEntry, List<FileListener>>(path, new FileEntry(file), new CopyOnWriteArrayList<FileListener>()) ;
				listeners.put(path, stamp) ;
			}
			stamp.getThird().add(listener) ;
		} catch(Exception e){throw new IllegalStateException(e);}
	}
	
	
	
	
}
