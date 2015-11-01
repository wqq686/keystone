package com.keystone.support.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtils {

	
	
	/**
	 * Get host IP address
	 * 
	 * @return IP Address
	 */
	public static List<String> getLocalHosts() {
		List<String> list = new ArrayList<>() ;
		try
		{
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nets.nextElement();
				Enumeration<InetAddress> itor = ni.getInetAddresses();
				while (itor.hasMoreElements()) {
					InetAddress inetAddress = itor.nextElement();
					String host = inetAddress.getHostName() ;
					list.add(host) ;
				}
			}
		} catch (Exception e) {throw new IllegalStateException(e);}
		return list ;
	}

	
	


}
