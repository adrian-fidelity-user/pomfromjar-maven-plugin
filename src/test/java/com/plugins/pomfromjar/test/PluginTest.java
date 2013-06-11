package com.plugins.pomfromjar.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.plugins.pomfromjar.mojo.PomDependencyMojo;
import com.plugins.pomfromjar.utils.ProxyServer;


/**
 * Copyright (c) 2012-2013 FMR LLC.
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * 
 * @author Adrian Ronayne
 *
 */

//To gaurantee that connection test is run first, run tests alphabetically
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PluginTest {

	private static final String PROXY_HOST = "";
	private static final String PROXY_PORT = "";
	private static final String SONATYPE_REPO = "http://repository.sonatype.org";
	
	/**
	 * Test if connection available
	 */
	@Test
	public void connectionTest(){
		
		  //Needed to access external repository if behind a proxy
		  if(!StringUtils.isEmpty(PROXY_HOST) && !StringUtils.isEmpty(PROXY_PORT)){
			 new ProxyServer(PROXY_HOST , PROXY_PORT);
		  }
		
		  try {
			URL url = new URL(SONATYPE_REPO);
			URLConnection connection = url.openConnection();
			connection.getInputStream();
			
			if(connection.getContentLength() == -1){
				fail("Failed to verify connection, please check proxy settings if behind a firewall");
			}
		  } 
		  catch (IOException e) {
			  fail("Failed to open a connection, please check proxy settings if behind a firewall");
			  e.printStackTrace();
		  }

	}
	
	/**
	 * A high level test to check the plugin does not throw any exceptions
	 * For this test to successfully run the required dir structure needs 
	 * to be setup on local machine - refer to readme on project site
	 * 
	 * Note : if needed add proxy settings to constructor
	 */
	@Test
	public void downloadTest() {

		PomDependencyMojo pomDependencyMojo = new PomDependencyMojo("c:\\pom\\generated-dependencies-pom.xml" , 
				"c:\\pom\\jars" , SONATYPE_REPO+"/service/local/lucene/search?sha1=",
				PROXY_HOST , PROXY_PORT);
		 
		try {
			pomDependencyMojo.execute();
		} catch (MojoExecutionException e) {
			fail("MojoExecutionException");
			e.printStackTrace();
		} catch (MojoFailureException e) {
			fail("MojoFailureException");
			e.printStackTrace();
		}

	}

}
