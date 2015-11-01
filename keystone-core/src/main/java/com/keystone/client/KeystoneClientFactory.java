package com.keystone.client;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KeystoneClientFactory {

	
	/**
	 * 
	 */
	private static ConcurrentMap<String, KeystoneClient> clients = new ConcurrentHashMap<String, KeystoneClient>() ;
	
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param connectTimeout
	 * @param clientConfig
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static KeystoneClient getKeystoneClient(String host, int port, long connectTimeout, ClientConfig clientConfig) throws IOException, InterruptedException {
		String key = (host + ":" + port).intern() ;
		KeystoneClient client = clients.get(key) ;
		if(client==null)
		{
			synchronized (key) 
			{
				client = clients.get(key) ;
				if(client==null)
				{
					client = new KeystoneClient(host, port, connectTimeout, connectTimeout, clientConfig) ;
				}
				clients.put(key, client) ;
			}
		}
		return client ;
	}
	
	/**
	private static ConcurrentMap<String, KeystoneClient[]> clients00 = new ConcurrentHashMap<>() ;
	private static AtomicLong seq = new AtomicLong(0);
	public static KeystoneClient getKeystoneClient(String host, int port, long connectTimeout, ClientConfig clientConfig) throws IOException, InterruptedException {
		String key = (host + ":" + port).intern() ;
		KeystoneClient[] clients0 = clients00.get(key) ;
		if(clients0==null)
		{
			synchronized (key) 
			{
				clients0 = clients00.get(key) ;
				if(clients0==null)
				{
					clients0 = new KeystoneClient[4] ;
					for(int i=0; i<4; i++) {
						KeystoneClient client = new KeystoneClient(host, port, connectTimeout, connectTimeout, clientConfig) ;
						clients0[i] = client ;
					}
				}
				clients00.put(key, clients0) ;
			}
		}
		return clients0[(int)((seq.incrementAndGet()& 0x7FFFFFFFFFFFFFFFL) % clients0.length)];
	}*/


}
