package com.keystone.remoting.locator;

import java.io.IOException;

import com.keystone.client.ClientConfig;
import com.keystone.client.KeystoneClient;
import com.keystone.client.KeystoneClientFactory;
import com.keystone.share.locations.ServiceLocation;

public class RemotingProxy {

	/** */
	private static final ClientConfig DEFAULT_CLIENT_CONFIG = new ClientConfig() ;

	ServiceLocation location ;

	/** */
	private KeystoneClient client = null ;
	
	public RemotingProxy(ServiceLocation location) throws IOException, InterruptedException {
		this.location = location ;
		initClient(); 
	}
	
	
	/**
	public RemotingProxy(String business, String serviceName, String host, int port, String contextName, int connectTimeout, int readTimeout) throws IOException, InterruptedException {
		this.business = business ;
		this.serviceName = serviceName ;
		this.host = host ;
		this.port = port ;
		this.contextName = contextName;
		this.connectTimeout = connectTimeout ;
		this.readTimeout = readTimeout ;
		initClient(); 
	}*/
	
	
	private void initClient() throws IOException, InterruptedException {
		this.client = KeystoneClientFactory.getKeystoneClient(location.getHost(), location.getPort(), location.getConnectTimeout(), DEFAULT_CLIENT_CONFIG) ;
	}


	public ServiceLocation getLocation() {
		return location;
	}


	public void setLocation(ServiceLocation location) {
		this.location = location;
	}


	public KeystoneClient getClient() {
		return client;
	}


	public void setClient(KeystoneClient client) {
		this.client = client;
	}


	@Override
	public String toString() {
		return "RemotingProxy [location=" + location + ", client=" + client + "]";
	}
	

	
	
}
