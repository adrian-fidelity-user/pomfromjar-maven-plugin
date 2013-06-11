package com.plugins.pomfromjar.utils;

/**
 * Copyright (c) 2012-2013 FMR LLC.
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * If user is behind a firewell then may need to set proxy parameters
 * @author Adrian Ronayne
 *
 */
public class ProxyServer {
	
	private String proxyHost;
	private String proxyPort;
	
	public ProxyServer(String proxyHost, String proxyPort){
		
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.setProxyServerSettings();
		
	}

	/**
	 * Set the proxy settings
	 */
	private void setProxyServerSettings(){		
		  System.setProperty("http.proxyHost", this.proxyHost);
		  System.setProperty("http.proxyPort", this.proxyPort);	
	}

}
