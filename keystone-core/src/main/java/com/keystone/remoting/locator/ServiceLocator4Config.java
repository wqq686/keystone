package com.keystone.remoting.locator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keystone.share.locations.Locators;
import com.keystone.share.locations.ServiceLocation;
import com.keystone.share.locations.ServiceLocator;
import com.keystone.support.common.ExceptionUtils;
import com.keystone.support.common.IOUtils;
import com.keystone.support.common.NumberUtils;
import com.keystone.support.utils.XmlElement;

/**
 * 
 * @author wuqq
 *
 */
public class ServiceLocator4Config implements ServiceLocator {

	/** */
	private static final String LOCAL_CONFIG_NAME = "ks-remoting.xml" ;

	/** */
	private Map<String, LocationConfig> configContainer = new HashMap<String, LocationConfig>() ;
	
	/** */
	private String zookeeperLocation ;
	
	/**
	 * 
	 */
	public ServiceLocator4Config() {
		loadConfig();
	}
	
	
	
	
	@Override
	public List<ServiceLocation> locate(String business, String service) {
		List<ServiceLocation> locations = null ;
		String key = Locators.getServiceLocationKey(business, service) ;
		LocationConfig config = configContainer.get(key) ;
		if(config!=null)
		{
			locations = createLocations(config) ;
		}
		return locations ;
	}
	

	
	
	public String getZookeeperLocation() {
		return zookeeperLocation;
	}





	private List<ServiceLocation> createLocations(LocationConfig config) {
		try
		{
			List<ServiceLocation> locations = new ArrayList<>() ;
			if(config!=null) {
				int connectTimeout = config.connTimeout, readTimeout = config.readTimeout ; String[] addresses = config.location.split(";") ;
				for(String address : addresses) {
					String[] array = parseLocationAddress(address) ;
					String host = array[0]; int port = Integer.valueOf(array[1]) ; String context = array[2] ;
					
					ServiceLocation location = new ServiceLocation(config.business, config.service, host, port, context, connectTimeout, readTimeout) ;
					locations.add(location) ;
				}
			}
			return locations ;
		}catch(Exception e){ throw new IllegalStateException(e) ;}
	}
	
	
	
	
	
	
	private synchronized void loadConfig() {
		try
		{
			InputStream is = IOUtils.getInputStreamFromClassPath(LOCAL_CONFIG_NAME) ;
			if(is==null) throw new FileNotFoundException(LOCAL_CONFIG_NAME) ;
			XmlElement root = XmlElement.read(is) ;
			XmlElement zookeeper = root.element("zookeeper") ;
			zookeeperLocation = zookeeper.attributeValue("location") ;
			
			List<XmlElement> businesses = root.elements("business") ;
			for(XmlElement b : businesses)
			{
				String business = b.attributeValue("name") ;
				List<XmlElement> services = b.elements("service") ;
				for(XmlElement s : services)
				{
					String service = s.attributeValue("name") ;
					int connTimeout = NumberUtils.parseInt(s.elementText("connTimeout"), 1000) ;
					int readTimeout = NumberUtils.parseInt(s.elementText("readTimeout"), 10000) ; 
					String location = s.elementText("locator") ;
					
					String key = Locators.getServiceLocationKey(business, service) ;
					LocationConfig config = new LocationConfig(business, service, connTimeout, readTimeout, location) ;
					configContainer.put(key, config) ;
				}
			}
			System.out.println("[ks-remoting's services config]:"+configContainer);
		} catch(Throwable t) {
			ExceptionUtils.fullError("load ["+LOCAL_CONFIG_NAME+"] with ex:", t) ;
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param locationAddress
	 * @return
	 */
	String[] parseLocationAddress(String locationAddress) {
		String[] array = locationAddress.split(":") ;
		String host = array[0].trim(), other = array[1].trim(), port="", appname = "" ;
		int index = other.indexOf("/") ;
		if(index>-1 && index<other.length()-1)
		{
			port = other.substring(0, index).trim() ;
			appname = other.substring(index+1).trim() ;
		}
		return new String[]{host, port, appname} ;
	}
	
	
	
	
	static class LocationConfig {
		String business ;
		String service ;
		int connTimeout = 1000 ;
		int readTimeout = 10000 ;
		String location = null ;
		
		public LocationConfig(String business, String service, int connTimeout, int readTimeout, String location) {
			this.business = business;
			this.service = service;
			this.connTimeout = connTimeout;
			this.readTimeout = readTimeout;
			this.location = location;
		}

		@Override
		public String toString() {
			return "LocatorConfig [business=" + business + ", service=" + service + ", connTimeout=" + connTimeout + ", readTimeout=" + readTimeout + ", location=" + location + "]";
		}
	}




	

}
